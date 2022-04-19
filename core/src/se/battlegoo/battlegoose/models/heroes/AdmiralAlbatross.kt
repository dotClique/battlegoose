package se.battlegoo.battlegoose.models.heroes

import se.battlegoo.battlegoose.models.spells.AdrenalineBoostSpell

class AdmiralAlbatross : Hero(
    HeroStats(),
    AdrenalineBoostSpell(),
    "Admiral Albatross",
    "\"I'm an Albatross!\"",
    "heroes/admiral_albatross.png"
)
