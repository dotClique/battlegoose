package se.battlegoo.battlegoose.models.units

import se.battlegoo.battlegoose.models.heroes.Hero

class SpitfireSeagull(hero: Hero) : UnitModel(
    hero,
    UnitStats(70, 20, 5, 3, 3, true), "Spitfire Seagull", "[Missing Description]"
)
