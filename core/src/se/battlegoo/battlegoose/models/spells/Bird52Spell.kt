package se.battlegoo.battlegoose.models.spells

import se.battlegoo.battlegoose.datamodels.SpellData

class Bird52Spell : Spell<SpellData>(
    "Bird-52",
    "Deal 30 damage to all units in the middle 2 columns of the battlefield.",
    1,
    3
) {
    override fun cast(data: SpellData): ActiveSpell {
        return Bird52ActiveSpell(this, 2, 30)
    }
}
