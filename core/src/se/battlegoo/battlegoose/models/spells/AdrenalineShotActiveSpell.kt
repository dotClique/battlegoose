package se.battlegoo.battlegoose.models.spells

import se.battlegoo.battlegoose.datamodels.SpellData
import se.battlegoo.battlegoose.models.Battle
import se.battlegoo.battlegoose.models.heroes.HeroStatsModifier

class AdrenalineShotActiveSpell(
    baseSpell: AdrenalineShotSpell,
    override val data: SpellData.AdrenalineShot
) :
    ActiveSpell<AdrenalineShotSpell>(baseSpell, data) {

    override fun applyImplementation(battle: Battle, turnsSinceCast: Int) {
        battle.hero1.applyStatsModifier(
            HeroStatsModifier {
                it.copy(actionPoints = it.actionPoints + 1)
            }
        )
    }
}
