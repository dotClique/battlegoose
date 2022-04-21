package se.battlegoo.battlegoose.models.spells

class Bird52Spell : Spell(
    "Bird-52",
    "Deal 30 damage to all units in the middle 2 columns of the battlefield.",
    1,
    3
) {
    override fun cast(): ActiveSpell {
        return Bird52ActiveSpell(this, 2, 30)
    }
}
