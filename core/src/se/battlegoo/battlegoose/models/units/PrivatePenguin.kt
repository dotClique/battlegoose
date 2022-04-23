package se.battlegoo.battlegoose.models.units

import se.battlegoo.battlegoose.models.heroes.Hero

class PrivatePenguin(hero: Hero<*>) : UnitModel(
    hero,
    UnitStats(100, 30, 10, 2, 1, false), "Private Penguin", "[Missing Description]"
)
