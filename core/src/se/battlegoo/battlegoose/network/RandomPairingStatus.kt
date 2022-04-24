package se.battlegoo.battlegoose.network

sealed class RandomPairingStatus {
    object WaitingForOtherPlayer : RandomPairingStatus()
    object CreatedLobby : RandomPairingStatus()
    object JoinedLobby : RandomPairingStatus()
    object WaitingInQueue : RandomPairingStatus()
    object JoinedQueue : RandomPairingStatus()
    object FirstInQueue : RandomPairingStatus()
    object Failed : RandomPairingStatus()
    data class StartBattle(
        val playerID: String,
        val battleID: String,
        val isHost: Boolean
    ) :
        RandomPairingStatus()
}
