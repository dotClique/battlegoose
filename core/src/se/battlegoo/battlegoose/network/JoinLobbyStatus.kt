package se.battlegoo.battlegoose.network

import se.battlegoo.battlegoose.datamodels.LobbyData

sealed class JoinLobbyStatus() {
    data class Ready(val lobby: LobbyData) : JoinLobbyStatus()
    data class StartBattle(val lobby: LobbyData) : JoinLobbyStatus()
    object NotAccessible : JoinLobbyStatus()
    object Full : JoinLobbyStatus()
    object DoesNotExist : JoinLobbyStatus()
}
