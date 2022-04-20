package se.battlegoo.battlegoose.models.spells

class AdrenalineShotSpell : Spell(
    "Adrenaline Shot",
    "Get an extra action point every turn for the following 3 turns.",
    3,
    6
) {
    override fun cast(): ActiveSpell {
        return AdrenalineShotActiveSpell(this)
    }
}
