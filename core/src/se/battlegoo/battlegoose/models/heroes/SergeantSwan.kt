package se.battlegoo.battlegoose.models.heroes

import se.battlegoo.battlegoose.models.spells.EphemeralAllegianceSpell
import se.battlegoo.battlegoose.models.units.DelinquentDuck
import se.battlegoo.battlegoose.models.units.GuardGoose
import se.battlegoo.battlegoose.models.units.PrivatePenguin
import se.battlegoo.battlegoose.models.units.SpitfireSeagull

class SergeantSwan : Hero<EphemeralAllegianceSpell>(
    HeroStats(),
    EphemeralAllegianceSpell(),
    "Sergeant Swan",
    "The Sergeant has a fabulous backstory, which " +
        "unfortunately cannot fit in this description.",
    HeroSprite.SERGEANT_SWAN,
    listOf(
        DelinquentDuck::class,
        GuardGoose::class,
        SpitfireSeagull::class,
        SpitfireSeagull::class,
        PrivatePenguin::class,
        PrivatePenguin::class,
    )
)
