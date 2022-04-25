package se.battlegoo.battlegoose.views

enum class FacingDirection {
    RIGHT,
    LEFT;

    fun flipped(): FacingDirection = when (this) {
        RIGHT -> LEFT
        LEFT -> RIGHT
    }
}
