package se.battlegoo.battlegoose.models.spells

import se.battlegoo.battlegoose.datamodels.SpellData
import se.battlegoo.battlegoose.models.Battle
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.models.heroes.HeroStatsModifier

class AdrenalineShotActiveSpell(
    baseSpell: AdrenalineShotSpell,
    caster: Hero,
    override val data: SpellData.AdrenalineShot
) : ActiveSpell<AdrenalineShotSpell>(baseSpell, caster, data) {

    override fun applyImplementation(battle: Battle, turnsSinceCast: Int) {
        caster.applyStatsModifier(
            HeroStatsModifier {
                it.copy(actionPoints = it.actionPoints + 1)
            }
        )
    }
}
