package se.battlegoo.battlegoose.models.heroes

import se.battlegoo.battlegoose.models.Action
import se.battlegoo.battlegoose.models.spells.Spell

abstract class Hero(
    val heroId: Int,
    val baseStats: HeroStats,
    val spell: Spell,
    val name: String,
    val description: String,
    heroSprite: HeroSprite
) {
    val texturePath: String = when (heroSprite) {
        HeroSprite.SERGEANT_SWAN -> "heroes/sergeant_swan.png"
        HeroSprite.MAJOR_MALLARD -> "heroes/major_mallard.png"
        HeroSprite.ADMIRAL_ALBATROSS -> "heroes/admiral_albatross.png"
    }

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

enum class HeroSprite {
    SERGEANT_SWAN,
    MAJOR_MALLARD,
    ADMIRAL_ALBATROSS
}
