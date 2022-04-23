package se.battlegoo.battlegoose.models.spells

import se.battlegoo.battlegoose.datamodels.SpellData
import se.battlegoo.battlegoose.models.Battle
import se.battlegoo.battlegoose.models.units.UnitModel
import java.lang.IllegalStateException

class EphemeralAllegianceActiveSpell(
    baseSpell: EphemeralAllegianceSpell,
    override val data: SpellData.EphemeralAllegiance
) : ActiveSpell<EphemeralAllegianceSpell>(baseSpell, data) {

    private lateinit var convertedUnit: UnitModel

    override fun applyImplementation(battle: Battle, turnsSinceCast: Int) {
        when (turnsSinceCast) {
            0 -> convertUnit(battle)
            baseSpell.duration - 1 -> convertedUnit.allegiance = convertedUnit.owner
        }
    }

    private fun convertUnit(battle: Battle) {
        convertedUnit = battle.battleMap.getUnit(data.targetPosition)
            ?: throw IllegalStateException("No unit on target tile ${data.targetPosition}")
        if (convertedUnit.allegiance == battle.hero1) {
            throw IllegalStateException("Unit on target tile already belongs to this player")
        }
        convertedUnit.allegiance = battle.hero1
    }
}
