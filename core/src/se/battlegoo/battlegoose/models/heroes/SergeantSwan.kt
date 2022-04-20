package se.battlegoo.battlegoose.models.heroes

import se.battlegoo.battlegoose.models.spells.AdrenalineBoostSpell

class SergeantSwan : Hero(
    HeroStats(),
    AdrenalineBoostSpell(),
    "Sergeant Swan",
    "The Sergeant has a fabulous backstory, which " +
        "unfortunately cannot fit in this description.",
    HeroSprite.SERGEANT_SWAN
)
