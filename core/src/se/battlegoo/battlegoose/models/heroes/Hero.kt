package se.battlegoo.battlegoose.models.heroes

import se.battlegoo.battlegoose.datamodels.ActionData
import se.battlegoo.battlegoose.models.spells.Spell
import se.battlegoo.battlegoose.models.units.UnitModel
import kotlin.reflect.KClass

abstract class Hero<T : Spell<*>> (
    val baseStats: HeroStats,
    val spell: T,
    val name: String,
    val description: String,
    val heroSprite: HeroSprite,
    val army: List<KClass<out UnitModel>>
) {

    var currentStats: HeroStats = baseStats
        private set

    fun nextTurn() {
        applyStatsModifier(
            HeroStatsModifier {
                it.copy(actionPoints = baseStats.actionPoints)
            }
        )
    }

    fun performAction(action: ActionData) {
        if (action.actionPointCost > currentStats.actionPoints)
            throw IllegalStateException("Not enough action points")
        applyStatsModifier(
            HeroStatsModifier {
                it.copy(actionPoints = it.actionPoints - action.actionPointCost)
            }
        )
    }

    fun applyStatsModifier(modifier: HeroStatsModifier) {
        currentStats = modifier.apply(currentStats)
    }
}

enum class HeroSprite {
    SERGEANT_SWAN,
    MAJOR_MALLARD,
    ADMIRAL_ALBATROSS
}
