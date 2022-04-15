package se.battlegoo.battlegoose.datamodels

data class LobbyData(
    val hostID: String,
    val otherPlayerID: String = "",
    val shouldStart: Boolean = false,
    val battleID: String = ""
)
