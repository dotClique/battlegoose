package com.progark.battlegoose.models.units

import kotlin.math.ceil
import kotlin.math.roundToInt

class UnitStats(var maxHealth: Int, var attack: Int, var defense: Int, var speed: Int, var range: Int, var isFlying: Boolean ) {
    var health: Int = maxHealth;

    fun getMaxAttackRange(): Int {
        return (speed + range);
    }

    fun getIsFlying() :Boolean {
        return isFlying;
    }

    fun heal(healAmount: Int) {
        health += healAmount;
        if (health > maxHealth) health = maxHealth;
    }

    /**
     * Can never take zero damage (this is to avoid deadlock)
     */
    fun flatReduceIncomingDamage(incomingDamage : Int) {
        health -= if (incomingDamage - defense > 0) {
            (incomingDamage - defense)
        } else {
            1
        };
    }

    /**
     * Reduces damage by treating defense as a linearly scaling percent damage reduction.
     * Rounded to nearest whole damage point.
     * For example: Incoming 40 damage with 11 armor means 11% reduction from 40, which equals 35.6. Rounded to 36 damage taken
     * Can per request allow 100% damage mitigation
     */
    fun linearReduceIncomingDamage(incomingDamage: Int) {
        health -= ( incomingDamage * (100.0f - defense)).roundToInt();
    }

    /**
     * Usual MMORPG damage calc. Treat defense as percentage effective health
     * Rounds up to avoid deadlock situations
     */
    fun scaledReduceIncomingDamage(incomingDamage: Int) {
        health -= ceil(incomingDamage * (100.0f / (100.0f + defense))).toInt();
    }

    fun takeTrueDamage(incomingDamage: Int) {
        health -= incomingDamage;
    }
}