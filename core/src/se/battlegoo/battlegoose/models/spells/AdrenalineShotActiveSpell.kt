package se.battlegoo.battlegoose.models.spells

import se.battlegoo.battlegoose.models.Battle
import se.battlegoo.battlegoose.models.heroes.HeroStatsModifier

class AdrenalineShotActiveSpell(baseSpell: AdrenalineShotSpell) :
    ActiveSpell(3, baseSpell) {

    override fun applyImplementation(battle: Battle, turnsSinceCast: Int) {
        battle.hero1.applyStatsModifier(
            HeroStatsModifier {
                it.copy(actionPoints = it.actionPoints + 1)
            }
        )
    }
}
