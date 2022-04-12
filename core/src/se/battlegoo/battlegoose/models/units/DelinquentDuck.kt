package se.battlegoo.battlegoose.models.units

import se.battlegoo.battlegoose.models.heroes.Hero

class DelinquentDuck(hero: Hero) : UnitModel(
    hero,
    UnitStats(80, 40, 0, 3, 1, false), "Delinquent Duck", "[Missing Description]"
)
