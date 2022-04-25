package se.battlegoo.battlegoose.models.heroes

import se.battlegoo.battlegoose.models.spells.Bird52Spell
import se.battlegoo.battlegoose.models.units.DelinquentDuck
import se.battlegoo.battlegoose.models.units.PrivatePenguin
import se.battlegoo.battlegoose.models.units.SpitfireSeagull

class MajorMallard : Hero(
    HeroStats(),
    Bird52Spell(),
    "Major Mallard",
    "Mallard, Mallard!",
    HeroSprite.MAJOR_MALLARD,
    listOf(
        DelinquentDuck::class,
        DelinquentDuck::class,
        DelinquentDuck::class,
        SpitfireSeagull::class,
        PrivatePenguin::class,
        PrivatePenguin::class,
    )
)
