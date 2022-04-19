package se.battlegoo.battlegoose.models.spells

class AdrenalineBoostSpell : Spell(
    "Adrenaline Boost",
    "Get an extra action point every turn for the following 3 turns.",
    9
) {

    override fun cast(): ActiveSpell {
        return AdrenalineBoostActiveSpell(this)
    }
}
