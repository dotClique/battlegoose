package se.battlegoo.battlegoose.models.heroes

import se.battlegoo.battlegoose.models.spells.AdrenalineBoostSpell

class AdmiralAlbatross : Hero(
    3,
    HeroStats(),
    AdrenalineBoostSpell(),
    "Admiral Albatross",
    "\"I'm an Albatross!\"",
    HeroSprite.ADMIRAL_ALBATROSS
)
