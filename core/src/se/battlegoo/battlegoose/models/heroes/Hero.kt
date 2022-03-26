package se.battlegoo.battlegoose.models.heroes

import se.battlegoo.battlegoose.models.Action
import se.battlegoo.battlegoose.models.spells.Spell

abstract class Hero(
    val baseStats: HeroStats,
    val spell: Spell,
    val name: String,
    val description: String
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

    fun performAction(action: Action) {
        assert(action.actionPointCost <= currentStats.actionPoints) { "Not enough action points" }
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
