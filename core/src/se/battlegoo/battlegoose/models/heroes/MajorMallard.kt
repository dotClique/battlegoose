package se.battlegoo.battlegoose.models.heroes

import se.battlegoo.battlegoose.models.spells.Bird52Spell

class MajorMallard : Hero<Bird52Spell>(
    HeroStats(),
    Bird52Spell(),
    "Major Mallard",
    "Mallard, Mallard!",
    HeroSprite.MAJOR_MALLARD
)
