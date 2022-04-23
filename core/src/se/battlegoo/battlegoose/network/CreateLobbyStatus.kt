package se.battlegoo.battlegoose.network

enum class CreateLobbyStatus(val message: String) {
    OTHER_PLAYER_JOINED("Another player has joined. Ready to start battle"),
    OPEN("Waiting for another player")
}
