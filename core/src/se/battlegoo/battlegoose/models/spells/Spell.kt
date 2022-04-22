package se.battlegoo.battlegoose.models.spells

import se.battlegoo.battlegoose.datamodels.SpellData

abstract class Spell<T : SpellData>(
    val title: String,
    val description: String,
    val duration: Int,
    val cooldown: Int
) {
    abstract fun cast(data: T): ActiveSpell<Spell<*>>
}
