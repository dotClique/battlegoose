package se.battlegoo.battlegoose.controllers

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.GridVector
import se.battlegoo.battlegoose.ScreenVector
import se.battlegoo.battlegoose.models.BattleMap
import se.battlegoo.battlegoose.models.Obstacle
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.models.units.UnitModel
import se.battlegoo.battlegoose.views.BattleMapTileView
import se.battlegoo.battlegoose.views.BattleMapView
import se.battlegoo.battlegoose.views.ObstacleView
import kotlin.math.min
import kotlin.math.sqrt

class BattleMapController(
    private val hero: Hero,
    private val model: BattleMap,
    private val view: BattleMapView
) :
    ControllerBase(view) {

    val mapSize by model::gridSize

    // Radius of a hexagon is the distance from center to vertex
    private val tileHexRadius = min(
        // Width of single tile is sqrt(3) radii, total +.5 tile to make space for the offset rows
        view.size.x / (sqrt(3f) * (mapSize.x + 0.5f)),
        // HEIGHT OF SINGLE TILE IS 2 RADII, BUT EACH OVERLAPS THE PREVIOUS 1/4, EXCEPT THE FIRST ONE ADDING 1/2 RADIUS
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

    fun addUnit(controller: UnitController, pos: GridVector) {
        unitControllers[controller.unitModel] = controller
        model.placeUnit(controller.unitModel, pos)
        val size = ScreenVector(tileHexRadius * sqrt(3f), tileHexRadius * 2)
        val pPos = toPixelPos(pos)
        controller.viewSize = size
        controller.viewPosition =
            ScreenVector(pPos.x, pPos.y + (size.y - controller.viewSize.y) / 2f)
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
        val unit = model.getUnit(gridPosition)
        if (unit?.hero == hero) {
            unitControllers[unit]?.let { unitController ->
                val oldSelected = tileController.selected
                for (tRow in tileControllers) {
                    for (tc in tRow) {
                        tc?.selected = false
                    }
                }
                for (uc in unitControllers.values) {
                    uc.selected = false
                }
                tileController.selected = !oldSelected
                unitController.selected = !oldSelected
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
