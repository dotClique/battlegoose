package se.battlegoo.battlegoose.models

import se.battlegoo.battlegoose.datamodels.GridVector
import se.battlegoo.battlegoose.models.units.UnitModel
import se.battlegoo.battlegoose.utils.gridmath.Direction
import se.battlegoo.battlegoose.utils.gridmath.neighbours

class BattleMap(
    val background: BattleMapBackground,
    val gridSize: GridVector
) : Iterable<GridVector> {
    private val units: Array<Array<UnitModel?>> =
        Array(gridSize.y) { arrayOfNulls<UnitModel?>(gridSize.x) }
    private val obstacles: Array<Array<Obstacle?>> =
        Array(gridSize.y) { arrayOfNulls<Obstacle?>(gridSize.x) }

    fun isValidPosition(pos: GridVector): Boolean {
        return 0 <= pos.y && pos.y < gridSize.y && 0 <= pos.x && pos.x < (gridSize.x - (pos.y % 2))
    }

    private fun validatePosition(pos: GridVector) {
        if (!isValidPosition(pos)) {
            throw IllegalArgumentException(
                "Invalid coordinate $pos for size $gridSize"
            )
        }
    }

    fun isValidUnitPlacement(pos: GridVector): Boolean {
        return isValidPosition(pos) &&
            !isObstacleAt(pos) &&
            !isUnitAt(pos)
    }

    private fun validatePlacement(pos: GridVector) {
        if (isUnitAt(pos)) throw IllegalStateException("Unit already placed at $pos")
        if (isObstacleAt(pos)) throw IllegalStateException("Obstacle already placed at $pos")
    }

    fun placeUnit(unit: UnitModel, pos: GridVector) {
        validatePlacement(pos)
        units[pos.y][pos.x] = unit
    }

    fun moveUnit(from: GridVector, to: GridVector) {
        if (isUnitAt(to) || isObstacleAt(to)) {
            throw IllegalStateException("Position $to already occupied")
        }
        getUnit(from).let {
            if (it != null) {
                placeUnit(it, to)
                units[from.y][from.x] = null
            } else {
                throw IllegalStateException("No unit to move from $from")
            }
        }
    }

    fun removeUnit(pos: GridVector) {
        if (!isUnitAt(pos)) {
            throw IllegalStateException("No unit at $pos")
        }
        units[pos.y][pos.x] = null
    }

    fun removeUnit(unit: UnitModel) {
        removeUnit(
            getPosOfUnit(unit) ?: throw IllegalStateException("Unit $unit is not on the map")
        )
    }

    fun getUnit(pos: GridVector): UnitModel? {
        validatePosition(pos)
        return units[pos.y][pos.x]
    }

    fun getPosOfUnit(unit: UnitModel): GridVector? {
        for (y in 0 until gridSize.y) {
            for (x in 0 until gridSize.x - (y % 2)) {
                val pos = GridVector(x, y)
                if (getUnit(pos) == unit) {
                    return GridVector(x, y)
                }
            }
        }
        return null
    }

    fun getNeighboursOfPos(pos: GridVector): Map<Direction, GridVector> {
        return neighbours(pos).filter { (_, pos) -> isValidPosition(pos) }
    }

    fun isUnitAt(pos: GridVector): Boolean = getUnit(pos) != null

    fun placeObstacle(obstacle: Obstacle, pos: GridVector) {
        validatePlacement(pos)
        obstacles[pos.y][pos.x] = obstacle
    }

    fun getObstacle(pos: GridVector): Obstacle? = obstacles[pos.y][pos.x]

    fun isObstacleAt(pos: GridVector): Boolean = getObstacle(pos) != null

    override fun iterator(): Iterator<GridVector> {
        return (0 until gridSize.y).map { y ->
            (0 until gridSize.x - (y % 2)).map { x ->
                GridVector(x, y)
            }
        }.flatten().iterator()
    }

    fun getUnits(): List<UnitModel> {
        return units.flatten().filterNotNull()
    }
}
