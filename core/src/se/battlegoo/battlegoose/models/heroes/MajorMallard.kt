package se.battlegoo.battlegoose.models.heroes

import se.battlegoo.battlegoose.models.spells.AdrenalineBoostSpell

class MajorMallard : Hero(
    HeroStats(),
    AdrenalineBoostSpell(),
    "Major Mallard",
    "Mallard, Mallard!",
    HeroSprite.MAJOR_MALLARD
)
