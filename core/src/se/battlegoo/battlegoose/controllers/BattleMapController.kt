package se.battlegoo.battlegoose.controllers

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.datamodels.GridVector
import se.battlegoo.battlegoose.datamodels.ScreenVector
import se.battlegoo.battlegoose.gridmath.findReachablePositions
import se.battlegoo.battlegoose.models.BattleMap
import se.battlegoo.battlegoose.models.Obstacle
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.models.units.UnitModel
import se.battlegoo.battlegoose.utils.Modal
import se.battlegoo.battlegoose.utils.ModalType
import se.battlegoo.battlegoose.views.BattleMapTileState
import se.battlegoo.battlegoose.views.BattleMapTileView
import se.battlegoo.battlegoose.views.BattleMapView
import se.battlegoo.battlegoose.views.ObstacleView
import kotlin.math.min
import kotlin.math.sqrt

sealed class ActionState {
    object Idle : ActionState()
    data class Selecting(
        val pos: GridVector,
        val tileController: BattleMapTileController,
        val unit: UnitModel
    ) : ActionState()
}

class BattleMapController(
    private val hero: Hero,
    private val model: BattleMap,
    private val view: BattleMapView,
    private val onMoveUnit: (fromPosition: GridVector, toPosition: GridVector) -> Unit,
    private val onAttackUnit: (attackerPosition: GridVector, targetPosition: GridVector) -> Unit
) : ControllerBase(view) {

    val mapSize by model::gridSize
    var yourTurn = false

    // Radius of a hexagon is the distance from center to vertex
    private val tileHexRadius = min(
        // Width of single tile is sqrt(3) radii, total +.5 tile to make space for the offset rows
        view.size.x / (sqrt(3f) * (mapSize.x + 0.5f)),
        // Height of single tile is 2 radii, but each overlaps the previous 1/4, except the first one adding 1/2 radius
        view.size.y / (2 * mapSize.y * 0.75f + 0.5f)
    )
    private val tilesSize = ScreenVector(
        tileHexRadius * sqrt(3f) * mapSize.x,
        tileHexRadius * (1.5f * mapSize.y + 0.5f)
    )

    private val tileControllers: Array<Array<BattleMapTileController?>> =
        Array(mapSize.y) { arrayOfNulls<BattleMapTileController?>(mapSize.x) }
    private val obstacleViews = arrayListOf<ObstacleView>()
    private val unitControllers = hashMapOf<UnitModel, UnitController>()

    private var actionState: ActionState = ActionState.Idle

    private val invalidSelectionsBeforeHint = 2
    private var invalidSelectionsCount = 0

    init {
        model.forEach { gridPos ->
            tileControllers[gridPos.y][gridPos.x] = BattleMapTileController(
                tileView = BattleMapTileView(tileHexRadius, toPixelPos(gridPos)),
                onTileClick = { selectTile(gridPos, it) }
            )
        }
    }

    private fun toPixelPos(pos: GridVector): ScreenVector = pos.let { (x, y) ->
        ScreenVector(
            (tileHexRadius * sqrt(3.0) * (x + 0.5 * (y and 1))).toFloat() +
                view.pos.x + (view.size.x - tilesSize.x) / 2f,
            tileHexRadius * 3f / 2 * y + view.pos.y + (view.size.y - tilesSize.y) / 2f
        )
    }

    private fun isEnemyUnitAt(pos: GridVector): Boolean {
        return model.getUnit(pos)?.let { it.allegiance != hero } ?: false
    }

    fun addUnit(controller: UnitController, pos: GridVector) {
        unitControllers[controller.unitModel] = controller
        controller.viewSize = ScreenVector(tileHexRadius * sqrt(3f), tileHexRadius * 2)
        model.placeUnit(controller.unitModel, pos)
        setUnitViewPosition(controller, pos)
    }

    private fun moveUnit(controller: UnitController, from: GridVector, to: GridVector) {
        model.moveUnit(from, to)
        setUnitViewPosition(controller, to)
        onMoveUnit(from, to)
    }

    fun moveUnit(fromPosition: GridVector, toPosition: GridVector) {
        moveUnit(
            getUnitControllerAt(fromPosition)
                ?: throw IllegalStateException("No unit at position $fromPosition to be moved"),
            fromPosition,
            toPosition
        )
    }

    private fun removeUnit(unitController: UnitController) {
        unitController.unitModel.let {
            model.removeUnit(it)
            unitControllers.remove(it)
        }
        unitController.dispose()
    }

    private fun setUnitViewPosition(controller: UnitController, pos: GridVector) {
        val pPos = toPixelPos(pos)
        controller.viewPosition =
            ScreenVector(pPos.x, pPos.y + (tileHexRadius * 2 - controller.viewSize.y) / 2f)
    }

    fun addObstacle(obstacleType: Obstacle, pos: GridVector) {
        val view = ObstacleView(obstacleType)
        val size = ScreenVector(tileHexRadius * sqrt(3f), tileHexRadius * 2)
        val pPos = toPixelPos(pos)
        view.size = size
        view.position = ScreenVector(pPos.x, pPos.y + (size.y - view.size.y) / 2f)
        obstacleViews.add(view)
        model.placeObstacle(obstacleType, pos)
    }

    private fun registerInvalidSelection() {
        invalidSelectionsCount++
        if (invalidSelectionsCount >= invalidSelectionsBeforeHint) {
            setShowTileSelectionHints(true)
        }
    }

    private fun registerValidSelection() {
        invalidSelectionsCount = 0
        setShowTileSelectionHints(false)
    }

    private fun selectTile(gridPosition: GridVector, tileController: BattleMapTileController) {
        when (tileController.state) {
            BattleMapTileState.NORMAL -> {
                model.getUnit(gridPosition).let { unitModel ->
                    if (unitModel == null || unitModel.allegiance != hero) {
                        registerInvalidSelection()
                    } else {
                        registerValidSelection()
                        clearTileStates()
                        actionState = ActionState.Selecting(gridPosition, tileController, unitModel)
                        showMoveAndAttackOptionsForSelectedUnit()
                        tileController.state = BattleMapTileState.FOCUSED
                        val unitController = getUnitControllerAt(gridPosition)
                            ?: throw IllegalStateException("Missing unit controller")
                        unitController.selected = true
                    }
                }
            }
            BattleMapTileState.FOCUSED -> {
                registerValidSelection()
                clearTileStates()
                actionState = ActionState.Idle
            }
            BattleMapTileState.MOVE_TARGET -> {
                registerValidSelection()
                clearTileStates()
                moveSelectedUnit(gridPosition)
                actionState = ActionState.Idle
            }
            BattleMapTileState.ATTACK_TARGET -> {
                registerValidSelection()
                clearTileStates()
                attackWithSelectedUnit(gridPosition)
                actionState = ActionState.Idle
            }
        }
    }

    fun reselectCurrentSelection() {
        actionState.let { state ->
            if (state is ActionState.Selecting) {
                clearTileStates()
                actionState = ActionState.Idle
                if (state.unit == model.getUnit(state.pos)) {
                    selectTile(state.pos, state.tileController)
                }
            }
        }
    }

    private fun findMoveTargets(
        pos: GridVector,
        unit: UnitModel
    ): List<GridVector> {
        return findReachablePositions(
            model, pos, unit.currentStats.speed, unit.currentStats.isFlying
        ).filter { model.isValidUnitPlacement(it) }
    }

    private fun findAttackTargets(
        pos: GridVector,
        unit: UnitModel
    ): List<GridVector> {
        return findReachablePositions(
            model, pos, unit.currentStats.range, true
        ).filter { isEnemyUnitAt(it) }
    }

    private fun showMoveAndAttackOptionsForSelectedUnit() {
        actionState.let { state ->
            if (state !is ActionState.Selecting) {
                throw IllegalStateException(
                    "Attempted to show move and attack options without selecting a unit"
                )
            }
            val unit = model.getUnit(state.pos)
                ?: throw IllegalStateException("No unit at given position")
            findMoveTargets(state.pos, unit).mapNotNull { getTileControllerAt(it) }
                .forEach { it.state = BattleMapTileState.MOVE_TARGET }
            findAttackTargets(state.pos, unit).mapNotNull { getTileControllerAt(it) }
                .forEach { it.state = BattleMapTileState.ATTACK_TARGET }
        }
    }

    private fun setShowTileSelectionHints(showHints: Boolean) {
        if (!showHints) {
            model.forEach { pos -> getTileControllerAt(pos)?.let { it.showHint = false } }
            return
        }
        val hintTiles = actionState.let { state ->
            when (state) {
                ActionState.Idle -> {
                    model.filter { pos ->
                        model.getUnit(pos).let { it != null && it.allegiance == hero }
                    }
                }
                is ActionState.Selecting -> {
                    val unit = model.getUnit(state.pos)
                        ?: throw IllegalStateException("No unit at selected position")
                    findMoveTargets(state.pos, unit)
                        .union(findAttackTargets(state.pos, unit))
                        .plus(state.pos)
                }
            }
        }
        model.forEach { pos -> getTileControllerAt(pos)?.let { it.showHint = pos in hintTiles } }
    }

    private fun moveSelectedUnit(gridPosition: GridVector) {
        if (!yourTurn) {
            Modal("Not so fast!", "It's not your turn", ModalType.Info()).show()
            return
        }
        actionState.let { state ->
            if (state !is ActionState.Selecting) {
                throw IllegalStateException("Attempted to move without selecting a unit")
            }
            val unitController = getUnitControllerAt(state.pos)
                ?: throw IllegalStateException("Missing unit controller")
            moveUnit(unitController, state.pos, gridPosition)
        }
    }

    private fun attackWithSelectedUnit(gridPosition: GridVector) {
        if (!yourTurn) {
            Modal("Not so fast!", "It's not your turn", ModalType.Info()).show()
            return
        }
        actionState.let { state ->
            if (state !is ActionState.Selecting) {
                throw IllegalStateException("Attempted to attack without selecting a unit")
            }
            val unitController = getUnitControllerAt(state.pos)
                ?: throw IllegalStateException("Missing attacker unit controller")
            val targetUnitController = getUnitControllerAt(gridPosition)
                ?: throw IllegalStateException("Missing target unit controller")
            attackUnit(unitController, targetUnitController)
            onAttackUnit(state.pos, gridPosition)
        }
    }

    private fun getUnitControllerAt(gridPosition: GridVector): UnitController? {
        return model.getUnit(gridPosition).let { unitControllers[it] }
    }

    private fun getTileControllerAt(gridPosition: GridVector): BattleMapTileController? {
        return tileControllers[gridPosition.y][gridPosition.x]
    }

    private fun clearTileStates() {
        for (tRow in tileControllers) {
            for (tc in tRow) {
                tc?.state = BattleMapTileState.NORMAL
            }
        }
        for (uc in unitControllers.values) {
            uc.selected = false
        }
    }

    private fun attackUnit(controller: UnitController, targetController: UnitController) {
        targetController.unitModel.takeAttackDamage(controller.unitModel.currentStats.attack)
        if (targetController.unitModel.isDead()) {
            removeUnit(targetController)
        }
    }

    fun attackUnit(attackingUnit: UnitModel, targetUnit: UnitModel) {
        val attackingController = unitControllers[attackingUnit]
            ?: throw IllegalStateException("No controller for attacking unit $attackingUnit")
        val targetController = unitControllers[targetUnit]
            ?: throw IllegalStateException("No controller for target unit $targetUnit")
        attackUnit(attackingController, targetController)
    }

    private fun updateFromModel() {
        // Wrap with toList to avoid ConcurrentModificationException when removing while iterating
        for (unitController in unitControllers.values.toList()) {
            val model = unitController.unitModel
            if (model.isDead()) {
                removeUnit(unitController)
                continue
            }
            if (unitController.converted != (model.allegiance != model.owner)) {
                unitController.converted = !unitController.converted
            }
        }
    }

    override fun update(dt: Float) {
        for (tRow in tileControllers) {
            for (tc in tRow) {
                tc?.update(dt)
            }
        }
        for (uc in unitControllers.values) {
            uc.update(dt)
        }
        updateFromModel()
    }

    override fun render(sb: SpriteBatch) {
        super.render(sb)
        for (tRow in tileControllers) {
            for (tc in tRow) {
                tc?.render(sb)
            }
        }
        for (ov in obstacleViews) {
            ov.render(sb)
        }
        for (uc in unitControllers.values) {
            uc.render(sb)
        }
        // Separate health bar rendering to render all health bars above all units
        unitControllers.values.forEach { it.renderHealthBar(sb) }
    }

    override fun dispose() {
        super.dispose()
        for (tRow in tileControllers) {
            for (tc in tRow) {
                tc?.dispose()
            }
        }
        for (ov in obstacleViews) {
            ov.dispose()
        }
        for (uc in unitControllers.values) {
            uc.dispose()
        }
    }
}
