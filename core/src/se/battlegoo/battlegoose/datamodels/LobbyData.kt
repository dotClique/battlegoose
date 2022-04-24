package se.battlegoo.battlegoose.datamodels

data class LobbyData(
    val lobbyID: String,
    val hostID: String,
    val otherPlayerID: String = "",
    val battleID: String = ""
) : DataModel
