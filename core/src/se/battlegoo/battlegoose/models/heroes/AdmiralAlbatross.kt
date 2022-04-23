package se.battlegoo.battlegoose.models.heroes

import se.battlegoo.battlegoose.models.spells.AdrenalineShotSpell

class AdmiralAlbatross : Hero<AdrenalineShotSpell>(
    HeroStats(),
    AdrenalineShotSpell(),
    "Admiral Albatross",
    "\"I'm an Albatross!\"",
    HeroSprite.ADMIRAL_ALBATROSS
)
