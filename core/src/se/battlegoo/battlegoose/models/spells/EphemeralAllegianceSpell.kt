package se.battlegoo.battlegoose.models.spells

import se.battlegoo.battlegoose.datamodels.SpellData

class EphemeralAllegianceSpell : Spell<SpellData.EphemeralAllegiance>(
    "Ephemeral Allegiance",
    "Convert 1 random unit from the opponent to control for 3 turns.",
    4, // Needs an extra (3+1) for cleanup
    5
) {
    override fun cast(data: SpellData.EphemeralAllegiance): ActiveSpell {
        return EphemeralAllegianceActiveSpell(this)
    }
}
