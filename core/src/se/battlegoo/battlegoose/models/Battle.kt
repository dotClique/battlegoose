package se.battlegoo.battlegoose.models

import se.battlegoo.battlegoose.models.heroes.Hero

class Battle(val hero1: Hero<*>, val hero2: Hero<*>, val battleMap: BattleMap)

enum class BattleOutcome(val scoreChange: Long) {
    VICTORY(1),
    TIE(0),
    DEFEAT(-1)
}
