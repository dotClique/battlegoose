package se.battlegoo.battlegoose.models.spells

abstract class Spell(
    val title: String,
    val description: String
) {
    abstract fun cast(): ActiveSpell
}
