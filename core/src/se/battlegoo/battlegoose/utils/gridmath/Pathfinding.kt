package se.battlegoo.battlegoose.utils.gridmath

import se.battlegoo.battlegoose.datamodels.GridVector
import se.battlegoo.battlegoose.models.BattleMap

fun findReachablePositions(
    battleMap: BattleMap,
    pos: GridVector,
    limit: Int,
    allowOccupiedAsIntermediary: Boolean
): MutableSet<GridVector> {
    val visited = mutableSetOf(pos)
    val fringes = List<MutableList<GridVector>>(limit + 1) { mutableListOf() }
    fringes[0].add(pos)
    for (k in 1..limit) {
        for (fPos in fringes[k - 1]) {
            for (neighbourPos in battleMap.getNeighboursOfPos(fPos).values) {
                if (visited.contains(neighbourPos)) continue
                visited.add(neighbourPos)
                if (
                    allowOccupiedAsIntermediary ||
                    (!battleMap.isObstacleAt(neighbourPos) && !battleMap.isUnitAt(neighbourPos))
                ) {
                    fringes[k].add(neighbourPos)
                }
            }
        }
    }
    visited.remove(pos) // don't include start position
    return visited
}
