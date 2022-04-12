package se.battlegoo.battlegoose.models

import se.battlegoo.battlegoose.GridVector
import se.battlegoo.battlegoose.models.units.UnitModel

class BattleMap(
    val backgroundPath: BattleMapBackground,
    val gridSize: GridVector
) {
    private val units: Array<Array<UnitModel?>> =
        Array(gridSize.y) { arrayOfNulls<UnitModel?>(gridSize.x) }
    private val obstacles: Array<Array<Obstacle?>> =
        Array(gridSize.y) { arrayOfNulls<Obstacle?>(gridSize.x) }

    private fun validatePosition(pos: GridVector) {
        if (pos.y > gridSize.y || pos.x > gridSize.x - (pos.y % 2)) {
            throw IllegalArgumentException(
                "Invalid coordinate $pos for size ($gridSize.x, $gridSize.y)"
            )
        }
        if (getUnit(pos) != null) {
            throw IllegalStateException("Unit already placed at $pos")
        }
        if (getObstacle(pos) != null) {
            throw IllegalStateException("Obstacle already placed at $pos")
        }
    }

    fun placeUnit(unit: UnitModel, pos: GridVector) {
        validatePosition(pos)
        units[pos.y][pos.x] = unit
    }

    fun getUnit(pos: GridVector): UnitModel? = units[pos.y][pos.x]

    fun placeObstacle(obstacle: Obstacle, pos: GridVector) {
        validatePosition(pos)
        obstacles[pos.y][pos.x] = obstacle
    }

    fun getObstacle(pos: GridVector): Obstacle? = obstacles[pos.y][pos.x]
}
