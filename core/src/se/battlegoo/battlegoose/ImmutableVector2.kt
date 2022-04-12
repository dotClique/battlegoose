package se.battlegoo.battlegoose

typealias ScreenVector = ImmutableVector2<Float>
typealias GridVector = ImmutableVector2<Int>

data class ImmutableVector2<T>(
    val x: T,
    val y: T
)
