package se.battlegoo.battlegoose.models.heroes

import se.battlegoo.battlegoose.models.spells.AdrenalineBoostSpell

class MajorMallard : Hero(
    HeroStats(),
    AdrenalineBoostSpell(),
    "Major Mallard",
    "Mallard, Mallard!",
    "heroes/major_mallard.png"
)
