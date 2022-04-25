package se.battlegoo.battlegoose.models.spells

import se.battlegoo.battlegoose.datamodels.SpellData
import se.battlegoo.battlegoose.models.heroes.Hero

abstract class Spell<T : SpellData>(
    val title: String,
    val description: String,
    val duration: Int,
    val cooldown: Int
) {
    abstract fun cast(caster: Hero, data: T): ActiveSpell<out Spell<T>>
}
