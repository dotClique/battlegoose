package se.battlegoo.battlegoose.models.spells

class EphemeralAllegianceSpell : Spell(
    "Ephemeral Allegiance",
    "Convert 1 random unit from the opponent to control for 3 turns.",
    4, // Needs an extra (3+1) for cleanup
    5
) {
    override fun cast(): ActiveSpell {
        return EphemeralAllegianceActiveSpell(this)
    }
}
