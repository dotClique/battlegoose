package se.battlegoo.battlegoose.network

import se.battlegoo.battlegoose.datamodels.BattleData

sealed class RandomPairingStatus {
    object WaitingForOtherPlayer : RandomPairingStatus()
    object CreatedLobby : RandomPairingStatus()
    object JoinedLobby : RandomPairingStatus()
    object WaitingInQueue : RandomPairingStatus()
    object JoinedQueue : RandomPairingStatus()
    object FirstInQueue : RandomPairingStatus()
    object Failed : RandomPairingStatus()
    data class StartBattle(
        val battle: BattleData,
        val isHost: Boolean
    ) :
        RandomPairingStatus()
}
