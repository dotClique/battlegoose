package se.battlegoo.battlegoose.models.spells

import se.battlegoo.battlegoose.datamodels.SpellData
import se.battlegoo.battlegoose.models.heroes.Hero

class EphemeralAllegianceSpell : Spell<SpellData.EphemeralAllegiance>(
    "Ephemeral Allegiance",
    "Convert 1 random unit from the opponent to control for 3 turns.",
    5, // Needs an extra (1+3+1) for cleanup, and one because the spell isn't helpful the first
    // turn when casting uses the (presumably) only action point
    5
) {
    override fun castImplementation(
        caster: Hero,
        data: SpellData.EphemeralAllegiance
    ): ActiveSpell<EphemeralAllegianceSpell> {
        return EphemeralAllegianceActiveSpell(this, caster, data)
    }
}
