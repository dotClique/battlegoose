package se.battlegoo.battlegoose.models.heroes

import se.battlegoo.battlegoose.models.spells.AdrenalineShotSpell

class MajorMallard : Hero(
    HeroStats(),
    AdrenalineShotSpell(),
    "Major Mallard",
    "Mallard, Mallard!",
    HeroSprite.MAJOR_MALLARD
)
