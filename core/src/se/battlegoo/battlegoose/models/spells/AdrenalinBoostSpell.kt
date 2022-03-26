package se.battlegoo.battlegoose.models.spells

class AdrenalinBoostSpell : Spell(
    "Adrenalin Boost",
    "Get an extra action point every turn for the following three turns"
) {

    override fun cast(): ActiveSpell {
        return AdrenalinBoostActiveSpell(this)
    }
}
