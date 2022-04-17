package se.battlegoo.battlegoose.models.heroes

import se.battlegoo.battlegoose.models.spells.AdrenalinBoostSpell

class SergeantSwan : Hero(
    HeroStats(),
    AdrenalinBoostSpell(),
    "Sergeant Swan",
    "The Sergeant has a fabulous backstory, which " +
        "unfortunately cannot fit in this description.",
    "heroes/sergeant_swan.png"
)
