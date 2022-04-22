package se.battlegoo.battlegoose.models.spells

import se.battlegoo.battlegoose.datamodels.SpellData

class AdrenalineShotSpell : Spell<SpellData.AdrenalineShot>(
    "Adrenaline Shot",
    "Get an extra action point every turn for the following 3 turns.",
    3,
    6
) {
    override fun cast(data: SpellData.AdrenalineShot): ActiveSpell<AdrenalineShotSpell> {
        return AdrenalineShotActiveSpell(this, data)
    }
}
