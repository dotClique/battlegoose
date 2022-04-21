package se.battlegoo.battlegoose.gridmath

import se.battlegoo.battlegoose.datamodels.GridVector

val NEIGHBOUR_DIFFERENCE_LOOKUP: Array<HashMap<Direction, GridVector>> = arrayOf(
    // even row (with yDown=false)
    hashMapOf(
        Pair(Direction.EAST, GridVector(1, 0)),
        Pair(Direction.NORTH_EAST, GridVector(0, 1)),
        Pair(Direction.NORTH_WEST, GridVector(-1, 1)),
        Pair(Direction.WEST, GridVector(-1, 0)),
        Pair(Direction.SOUTH_WEST, GridVector(-1, -1)),
        Pair(Direction.SOUTH_EAST, GridVector(0, -1)),
    ),
    // odd row (with yDown=false)
    hashMapOf(
        Pair(Direction.EAST, GridVector(1, 0)),
        Pair(Direction.NORTH_EAST, GridVector(1, 1)),
        Pair(Direction.NORTH_WEST, GridVector(0, 1)),
        Pair(Direction.WEST, GridVector(-1, 0)),
        Pair(Direction.SOUTH_WEST, GridVector(0, -1)),
        Pair(Direction.SOUTH_EAST, GridVector(1, -1)),
    )
)

fun neighbours(pos: GridVector): HashMap<Direction, GridVector> {
    val neighbours = hashMapOf<Direction, GridVector>()
    for ((direction, posDiff) in NEIGHBOUR_DIFFERENCE_LOOKUP[pos.y % 2]) {
        neighbours[direction] = GridVector(pos.x + posDiff.x, pos.y + posDiff.y)
    }
    return neighbours
}
