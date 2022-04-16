package se.battlegoo.battlegoose.network

import se.battlegoo.battlegoose.datamodels.LobbyData

sealed class LobbyStatus {
    data class Ready(val lobby: LobbyData) : LobbyStatus()
    object Full : LobbyStatus()
    object DoesNotExist : LobbyStatus()
}
