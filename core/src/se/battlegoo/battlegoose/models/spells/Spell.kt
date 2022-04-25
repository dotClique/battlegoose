package se.battlegoo.battlegoose.models.spells

import se.battlegoo.battlegoose.datamodels.SpellData
import se.battlegoo.battlegoose.models.heroes.Hero
import java.lang.Integer.max

abstract class Spell<T : SpellData>(
    val title: String,
    val description: String,
    val duration: Int,
    val cooldown: Int
) {
    var remainingCooldown: Int = 0
        private set

    fun decreaseCooldown() {
        remainingCooldown = max(0, remainingCooldown - 1)
    }

    fun cast(caster: Hero, data: T): ActiveSpell<out Spell<T>> {
        if (remainingCooldown > 0) {
            throw IllegalStateException("Remaining cooldown on $data for $caster, cannot cast")
        }
        remainingCooldown = cooldown
        return castImplementation(caster, data)
    }

    abstract fun castImplementation(caster: Hero, data: T): ActiveSpell<out Spell<T>>
}
