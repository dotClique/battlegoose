package se.battlegoo.battlegoose.models.spells

import com.badlogic.gdx.Gdx
import se.battlegoo.battlegoose.models.Battle
import se.battlegoo.battlegoose.models.units.UnitModel
import kotlin.random.Random

class EphemeralAllegianceActiveSpell(baseSpell: Spell) :
    ActiveSpell(baseSpell) {

    private lateinit var convertedUnit: UnitModel

    override fun applyImplementation(battle: Battle, turnsSinceCast: Int) {
        when (turnsSinceCast) {
            0 -> convertRandomUnit(battle)
            baseSpell.duration - 1 -> returnUnit(battle)
        }
    }

    private fun convertRandomUnit(battle: Battle) {
        // Retrieve all enemy units
        val enemyUnitPositions = battle.battleMap
            .filter { battle.battleMap.getUnit(it)?.owner == battle.hero2 }

        // TODO: Handle enemyUnitPositions.isEmpty() differently?
        if (enemyUnitPositions.isEmpty()) {
            Gdx.app.error("#ERROR", "No enemy units to convert")
            return
        }

        val unitPosition = enemyUnitPositions[Random.nextInt(0, enemyUnitPositions.size)]
        val unitToConvert = battle.battleMap.getUnit(unitPosition)!!

        convertedUnit = unitToConvert.convert(battle.hero1)

        battle.battleMap.replaceUnit(convertedUnit, unitPosition)
    }

    private fun returnUnit(battle: Battle) {
        val unitPosition = battle.battleMap
            .firstOrNull { battle.battleMap.getUnit(it) == convertedUnit }

        unitPosition?.let {
            battle.battleMap.replaceUnit(
                battle.battleMap.getUnit(it)!!.convert(battle.hero2),
                it
            )
        }
    }
}
