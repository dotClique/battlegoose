package se.battlegoo.battlegoose.models.heroes

import se.battlegoo.battlegoose.models.spells.AdrenalinBoostSpell

class MajorMallard : Hero(
    HeroStats(),
    AdrenalinBoostSpell(),
    "Major Mallard",
    "Mallard, Mallard!",
    "heroes/major_mallard.png"
)
