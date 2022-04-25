package se.battlegoo.battlegoose.models.spells

import se.battlegoo.battlegoose.datamodels.SpellData
import se.battlegoo.battlegoose.models.heroes.Hero

class AdrenalineShotSpell : Spell<SpellData.AdrenalineShot>(
    "Adrenaline Shot",
    "Get an extra action point every turn for the following 3 turns.",
    4, // 1 extra duration beecause the spell isn't helpful the first turn when casting uses
    // the (presumably) only action point
    6
) {
    override fun cast(
        caster: Hero,
        data: SpellData.AdrenalineShot
    ): ActiveSpell<AdrenalineShotSpell> {
        return AdrenalineShotActiveSpell(this, caster, data)
    }
}
