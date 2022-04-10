package se.battlegoo.battlegoose.models.units

data class UnitStats(
    val maxHealth: Int,
    val attack: Int,
    val defense: Int,
    val speed: Int,
    val range: Int,
    val isFlying: Boolean,
    val health: Int = maxHealth
)
