package se.battlegoo.battlegoose.models.heroes

import se.battlegoo.battlegoose.models.spells.AdrenalinBoostSpell

class AdmiralAlbatross : Hero(
    HeroStats(),
    AdrenalinBoostSpell(),
    "Admiral Albatross",
    "He's an albatross.",
    "heroes/admiral_albatross.png"
)
