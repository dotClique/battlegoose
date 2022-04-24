package se.battlegoo.battlegoose.models.spells

import se.battlegoo.battlegoose.datamodels.SpellData
import se.battlegoo.battlegoose.models.heroes.Hero

class EphemeralAllegianceSpell : Spell<SpellData.EphemeralAllegiance>(
    "Ephemeral Allegiance",
    "Convert 1 random unit from the opponent to control for 3 turns.",
    4, // Needs an extra (3+1) for cleanup
    5
) {
    override fun cast(
        caster: Hero,
        data: SpellData.EphemeralAllegiance
    ): ActiveSpell<EphemeralAllegianceSpell> {
        return EphemeralAllegianceActiveSpell(this, caster, data)
    }
}
