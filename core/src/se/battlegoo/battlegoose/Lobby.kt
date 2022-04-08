package se.battlegoo.battlegoose

data class Lobby(
    val hostID: String,
    val otherPlayerID: String = "",
    val shouldStart: Boolean = false
)
