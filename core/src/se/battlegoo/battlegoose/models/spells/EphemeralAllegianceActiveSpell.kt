package se.battlegoo.battlegoose.models.spells

import se.battlegoo.battlegoose.models.Battle
import se.battlegoo.battlegoose.models.units.UnitModel

class EphemeralAllegianceActiveSpell(baseSpell: EphemeralAllegianceSpell) :
    ActiveSpell(baseSpell) {

    private lateinit var convertedUnit: UnitModel

    override fun applyImplementation(battle: Battle, turnsSinceCast: Int) {
        when (turnsSinceCast) {
            0 -> convertRandomUnit(battle)
            baseSpell.duration - 1 -> convertedUnit.allegiance = convertedUnit.owner
        }
    }

    private fun convertRandomUnit(battle: Battle) {
        convertedUnit = battle.battleMap
            .mapNotNull { battle.battleMap.getUnit(it) }
            .filter { it.owner == battle.hero2 }
            .shuffled()
            .first()

        convertedUnit.allegiance = battle.hero1
    }
}
