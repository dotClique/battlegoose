package se.battlegoo.battlegoose.models.heroes

import se.battlegoo.battlegoose.models.spells.EphemeralAllegianceSpell

class SergeantSwan : Hero<EphemeralAllegianceSpell>(
    HeroStats(),
    EphemeralAllegianceSpell(),
    "Sergeant Swan",
    "The Sergeant has a fabulous backstory, which " +
        "unfortunately cannot fit in this description.",
    HeroSprite.SERGEANT_SWAN
)
