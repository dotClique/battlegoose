package se.battlegoo.battlegoose.models.heroes

import se.battlegoo.battlegoose.models.spells.AdrenalineShotSpell
import se.battlegoo.battlegoose.models.units.DelinquentDuck
import se.battlegoo.battlegoose.models.units.GuardGoose
import se.battlegoo.battlegoose.models.units.PrivatePenguin

class AdmiralAlbatross : Hero<AdrenalineShotSpell>(
    HeroStats(),
    AdrenalineShotSpell(),
    "Admiral Albatross",
    "\"I'm an Albatross!\"",
    HeroSprite.ADMIRAL_ALBATROSS,
    listOf(
        GuardGoose::class,
        GuardGoose::class,
        DelinquentDuck::class,
        DelinquentDuck::class,
        PrivatePenguin::class,
        PrivatePenguin::class,
    )
)
