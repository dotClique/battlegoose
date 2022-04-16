package se.battlegoo.battlegoose.models.units

import se.battlegoo.battlegoose.models.heroes.Hero

class GuardGoose(hero: Hero) : UnitModel(
    hero,
    UnitStats(110, 25, 50, 1, 1, false), "Guard Goose", "[Missing Description]"
)
