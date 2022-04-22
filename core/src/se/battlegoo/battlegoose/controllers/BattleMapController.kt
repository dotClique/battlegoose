package se.battlegoo.battlegoose.controllers

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.datamodels.GridVector
import se.battlegoo.battlegoose.datamodels.ScreenVector
import se.battlegoo.battlegoose.gridmath.findReachablePositions
import se.battlegoo.battlegoose.models.BattleMap
import se.battlegoo.battlegoose.models.Obstacle
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.models.units.UnitModel
import se.battlegoo.battlegoose.views.BattleMapTileState
import se.battlegoo.battlegoose.views.BattleMapTileView
import se.battlegoo.battlegoose.views.BattleMapView
import se.battlegoo.battlegoose.views.ObstacleView
import kotlin.math.min
import kotlin.math.sqrt

class BattleMapController(
    private val hero: Hero,
    private val model: BattleMap,
    private val view: BattleMapView
) : ControllerBase(view) {

    val mapSize by model::gridSize

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

    private var selectedTilePos: GridVector? = null

    init {
        for (y in 0 until mapSize.y) {
            for (x in 0 until mapSize.x - (y % 2)) {
                val gridPos = GridVector(x, y)
                tileControllers[gridPos.y][gridPos.x] = BattleMapTileController(
                    tileView = BattleMapTileView(tileHexRadius, toPixelPos(gridPos)),
                    onTileClick = { selectTile(gridPos, it) }
                )
            }
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

    private fun selectTile(gridPosition: GridVector, tileController: BattleMapTileController) {
        when (tileController.state) {
            BattleMapTileState.NORMAL -> showMoveAndAttackOptions(gridPosition, tileController)
            BattleMapTileState.FOCUSED -> clearTileStates()
            BattleMapTileState.MOVE_TARGET -> moveSelectedUnit(gridPosition)
            BattleMapTileState.ATTACK_TARGET -> attackWithSelectedUnit(gridPosition)
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

    private fun showMoveAndAttackOptions(pos: GridVector, tileController: BattleMapTileController) {
        val unit = model.getUnit(pos)
        if (unit?.allegiance == hero) {
            unitControllers[unit]?.let { unitController ->
                clearTileStates()
                for (mPos in findMoveTargets(pos, unit)) {
                    tileControllers[mPos.y][mPos.x]?.state = BattleMapTileState.MOVE_TARGET
                }
                for (aPos in findAttackTargets(pos, unit)) {
                    tileControllers[aPos.y][aPos.x]?.state = BattleMapTileState.ATTACK_TARGET
                }
                tileController.state = BattleMapTileState.FOCUSED
                unitController.selected = true
                selectedTilePos = pos
            }
        }
    }

    private fun moveSelectedUnit(gridPosition: GridVector) {
        selectedTilePos?.let { selectedTile ->
            getUnitControllerAt(selectedTile)?.let { unitController ->
                moveUnit(unitController, selectedTile, gridPosition)
                clearTileStates()
            }
        }
    }

    private fun attackWithSelectedUnit(gridPosition: GridVector) {
        selectedTilePos?.let { selectedTile ->
            getUnitControllerAt(selectedTile)?.let { unitController ->
                getUnitControllerAt(gridPosition)?.let { targetUnitController ->
                    attackUnit(unitController, targetUnitController)
                    clearTileStates()
                }
            }
        }
    }

    private fun getUnitControllerAt(gridPosition: GridVector): UnitController? {
        return model.getUnit(gridPosition).let { unitControllers[it] }
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
        selectedTilePos = null
    }

    private fun attackUnit(controller: UnitController, targetController: UnitController) {
        // TODO Actual attack logic...
        targetController.unitModel.takeAttackDamage(controller.unitModel.currentStats.attack)
//        removeUnit(targetController)
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
