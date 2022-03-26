package se.battlegoo.battlegoose.models.heroes

import se.battlegoo.battlegoose.models.spells.AdrenalinBoostSpell

class SeargentSwan : Hero(
    HeroStats(),
    AdrenalinBoostSpell(),
    "Seargent Swan",
    "The Seargent has a fabulous backstory, which " +
        "unfortunately cannot fit in this description."
)
