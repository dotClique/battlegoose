package se.battlegoo.battlegoose.models.spells

import se.battlegoo.battlegoose.datamodels.GridVector
import se.battlegoo.battlegoose.datamodels.SpellData
import se.battlegoo.battlegoose.models.Battle
import se.battlegoo.battlegoose.models.heroes.Hero
import kotlin.math.abs
import kotlin.math.floor

class Bird52ActiveSpell(
    baseSpell: Bird52Spell,
    caster: Hero,
    override val data: SpellData.Bird52
) : ActiveSpell<Bird52Spell>(baseSpell, caster, data) {

    override fun applyImplementation(battle: Battle, turnsSinceCast: Int) {
        // Hits a number of columns equaling numColumnsToAttack or numColumnsToAttack+1,
        // depending on row size parity
        (0 until battle.battleMap.gridSize.y).map { y ->
            val rowWidth = battle.battleMap.gridSize.x - (y % 2)
            val totalToHit = baseSpell.numColumnsToAttack +
                abs(baseSpell.numColumnsToAttack % 2 - rowWidth % 2)
            val splash = floor(totalToHit / 2f).toInt()
            val centerColumn = rowWidth / 2
            val firstX = centerColumn - splash
            val lastX = centerColumn + splash - ((rowWidth + 1) % 2)
            (firstX..lastX).map { x ->
                battle.battleMap.getUnit(GridVector(x, y))?.takeTrueDamage(baseSpell.attackDamage)
            }
        }
    }
}
