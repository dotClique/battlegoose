package se.battlegoo.battlegoose.models.spells

import se.battlegoo.battlegoose.models.Battle

abstract class ActiveSpell(
    val duration: Int,
    val baseSpell: Spell
) {
    private var turnsSinceCast: Int = 0
    var finished: Boolean = false
        private set

    fun apply(battle: Battle) {
        if (turnsSinceCast in 0 until duration) {
            applyImplementation(battle, turnsSinceCast)
            turnsSinceCast++
        }
        if (turnsSinceCast >= duration) {
            finished = true
        }
    }

    protected abstract fun applyImplementation(battle: Battle, turnsSinceCast: Int)
}
