package se.battlegoo.battlegoose.datamodels

import se.battlegoo.battlegoose.models.spells.AdrenalinBoostSpell
import se.battlegoo.battlegoose.models.spells.Spell

sealed class SpellData<out T> where T : Spell {

    val spellType: String = this::class.java.name

    object AdrenalinBoostSpellData : SpellData<AdrenalinBoostSpell>()
}
