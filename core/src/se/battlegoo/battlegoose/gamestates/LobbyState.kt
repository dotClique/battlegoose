package se.battlegoo.battlegoose.gamestates

import se.battlegoo.battlegoose.models.heroes.Hero

abstract class LobbyState(val selectedHero: Hero<*>) : GameState()
