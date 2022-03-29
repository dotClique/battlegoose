package com.progark.battlegoose.models.units

import kotlin.math.ceil
import kotlin.math.round

class UnitStats(maxHealth: Int, attack: Int, defense: Int, speed: Int, range: Int, isFlying: Boolean ) {
    var health: Int = maxHealth;
    var maxHealth: Int = maxHealth;
    var attack: Int = attack;
    var defense: Int = defense;
    var speed: Int = speed;
    var range: Int = range;
    var isFlying: Boolean = isFlying;

    public fun getMaxAttackRange(): Int {
        return (speed + range);
    }

    public fun getIsFlying() :Boolean {
        return isFlying;
    }

    public fun heal(healAmount: Int) {
        health += healAmount;
        if (health > maxHealth) health = maxHealth;
    }

    /**
     * Can never take zero damage (this is to avoid deadlock)
     */
    public fun flatReduceIncomingDamage(incomingDamage : Int) {
        health -= if (incomingDamage - defense > 0) {
            (incomingDamage - defense)
        } else {
            1
        }
    }

    /**
     * Rounds up to avoid deadlock situations
     */
    public fun scaledReduceIncomingDamage(incomingDamage: Int) {
        health -= ceil(incomingDamage * (100.0f / (100.0f + (defense as Float)))).toInt();
    }

    public fun takeTrueDamage(incomingDamage: Int) {
        health -= incomingDamage;
    }
}