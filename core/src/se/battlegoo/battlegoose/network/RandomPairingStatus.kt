package se.battlegoo.battlegoose.network

enum class RandomPairingStatus {
    WAITING_FOR_OTHER_PLAYER,
    CREATED_LOBBY,
    JOINED_LOBBY,
    WAITING_IN_QUEUE,
    JOINED_QUEUE,
    OTHER_PLAYER_JOINED,
    FIRST_IN_QUEUE,
    FAILED,
    START_BATTLE
}
