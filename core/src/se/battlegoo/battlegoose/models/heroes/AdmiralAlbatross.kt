package se.battlegoo.battlegoose.models.heroes

import se.battlegoo.battlegoose.models.spells.AdrenalinBoostSpell

class AdmiralAlbatross : Hero(
    HeroStats(),
    AdrenalinBoostSpell(),
    "Admiral Albatross",
    "\"I'm an Albatross!\"",
    "heroes/admiral_albatross.png"
)
