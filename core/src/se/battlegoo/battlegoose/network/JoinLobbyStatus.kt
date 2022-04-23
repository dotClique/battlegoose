package se.battlegoo.battlegoose.network

import se.battlegoo.battlegoose.datamodels.LobbyData

sealed class JoinLobbyStatus(val message: String = "") {
    data class Ready(val lobby: LobbyData) : JoinLobbyStatus("Ready to start battle")
    data class StartBattle(val lobby: LobbyData) : JoinLobbyStatus("Starting battle")
    object NotAccessable : JoinLobbyStatus("Lobby was not accessable")
    object Full : JoinLobbyStatus("Lobby is full. Try another lobby")
    object DoesNotExist : JoinLobbyStatus("The requested lobby does not exist")
}
