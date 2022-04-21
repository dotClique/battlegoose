package se.battlegoo.battlegoose.models.spells

import se.battlegoo.battlegoose.GridVector
import se.battlegoo.battlegoose.models.Battle

class Bird52ActiveSpell(baseSpell: Spell, val numColumnsToAttack: Int, val attackDamage: Int) :
    ActiveSpell(baseSpell) {

    override fun applyImplementation(battle: Battle, turnsSinceCast: Int) {
        // Leans left for odd grid width
        // Leans right for odd numColumnsToAttack
        val leftColumn: Int = battle.battleMap.gridSize.x / 2 + 1 - numColumnsToAttack / 2

        (0 until battle.battleMap.gridSize.y).map { y ->
            (leftColumn..leftColumn + 1).map { x ->
                GridVector(x, y)
            }
        }.flatten().forEach { battle.battleMap.getUnit(it)?.takeTrueDamage(attackDamage) }
    }
}
