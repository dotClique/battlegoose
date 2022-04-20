package se.battlegoo.battlegoose.models.heroes

import se.battlegoo.battlegoose.models.spells.AdrenalineBoostSpell

class MajorMallard : Hero(
    2,
    HeroStats(),
    AdrenalineBoostSpell(),
    "Major Mallard",
    "Mallard, Mallard!",
    HeroSprite.MAJOR_MALLARD
)
