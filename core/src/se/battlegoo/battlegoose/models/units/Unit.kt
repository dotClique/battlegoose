package com.progark.battlegoose.models.units

import kotlin.math.roundToInt

open class Unit(unitStats: UnitStats, val name: String, val description: String) {

    var currentStats: UnitStats = unitStats

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
