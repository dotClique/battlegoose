package se.battlegoo.battlegoose.models.units

import se.battlegoo.battlegoose.models.heroes.Hero
import kotlin.math.roundToInt

open class UnitModel(
    val hero: Hero,
    baseStats: UnitStats,
    val name: String,
    val description: String
) {

    var currentStats: UnitStats = baseStats
        private set

    fun applyModifier(modifier: UnitStatsModifier) {
        currentStats = modifier(currentStats)
    }

    fun isDead(): Boolean {
        return (currentStats.health <= 0)
    }

    fun takeAttackDamage(incomingDamage: Int) {
        applyModifier(fun(unitStats: UnitStats): UnitStats {
            return unitStats.copy(
                health = unitStats.health - incomingDamage
                    * (100.0f - currentStats.defense).roundToInt()
            )
        })
    }

    fun takeTrueDamage(incomingDamage: Int) {
        applyModifier(fun(unitStats: UnitStats): UnitStats {
            return unitStats.copy(health = unitStats.health - incomingDamage)
        })
    }

    fun heal(healAmount: Int) {
        applyModifier(fun(unitStats: UnitStats): UnitStats {
            return unitStats.copy(
                health = if (unitStats.health + healAmount >= unitStats.maxHealth)
                    unitStats.maxHealth else unitStats.health + healAmount
            )
        })
    }
}
