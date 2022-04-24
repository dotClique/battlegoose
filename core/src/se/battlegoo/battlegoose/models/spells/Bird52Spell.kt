package se.battlegoo.battlegoose.models.spells

import se.battlegoo.battlegoose.datamodels.SpellData
import se.battlegoo.battlegoose.models.heroes.Hero

class Bird52Spell : Spell<SpellData.Bird52>(
    "Bird-52",
    "Deal 30 damage to all units in the middle 2 columns of the battlefield.",
    1,
    3
) {
    val numColumnsToAttack = 2
    val attackDamage = 30

    override fun cast(caster: Hero, data: SpellData.Bird52): ActiveSpell<Bird52Spell> {
        return Bird52ActiveSpell(this, caster, data)
    }
}
