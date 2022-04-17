package se.battlegoo.battlegoose.network

enum class RandomOpponentStatus {
    WAITING_FOR_OTHER_PLAYER,
    CREATED_LOBBY,
    JOINED_LOBBY,
    WAITING_IN_QUEUE,
    JOIN_QUEUE,
    TIMEOUT_INACTIVE_PLAYER,
    OTHER_PLAYER_JOINED
}
