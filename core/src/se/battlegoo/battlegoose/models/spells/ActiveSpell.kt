package se.battlegoo.battlegoose.models.spells

import se.battlegoo.battlegoose.datamodels.SpellData
import se.battlegoo.battlegoose.models.Battle
import se.battlegoo.battlegoose.models.heroes.Hero

abstract class ActiveSpell<T : Spell<*>>(
    val baseSpell: T,
    val caster: Hero,
    open val data: SpellData
) {
    private var turnsSinceCast: Int = 0
    var finished: Boolean = false
        private set

    fun apply(battle: Battle) {
        if (turnsSinceCast in 0 until baseSpell.duration) {
            applyImplementation(battle, turnsSinceCast)
            turnsSinceCast++
        }
        if (turnsSinceCast >= baseSpell.duration) {
            finished = true
        }
    }

    protected abstract fun applyImplementation(battle: Battle, turnsSinceCast: Int)
}
