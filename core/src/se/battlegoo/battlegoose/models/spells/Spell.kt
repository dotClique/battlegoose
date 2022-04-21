package se.battlegoo.battlegoose.models.spells

abstract class Spell(
    val title: String,
    val description: String,
    val duration: Int,
    val cooldown: Int
) {
    abstract fun cast(): ActiveSpell
}
