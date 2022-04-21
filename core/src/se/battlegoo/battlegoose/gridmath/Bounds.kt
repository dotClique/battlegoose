package se.battlegoo.battlegoose.gridmath

import se.battlegoo.battlegoose.datamodels.ScreenVector
import kotlin.math.abs

fun isPointInsideHexagon(pos: ScreenVector, hexPos: ScreenVector, hexSize: ScreenVector): Boolean {
    val rX = hexSize.x / 2f // distance from center to left/right side
    val rY = hexSize.y / 2f // distance from center to pointy top/bottom
    val dcx = abs(pos.x - hexPos.x - rX) // x distance from center to point
    val dcy = abs(pos.y - hexPos.y - rY) // y distance from center to point
    return dcx <= rX && dcy <= rY && rY * rX - rY / 2f * dcx - rX * dcy >= 0
}
