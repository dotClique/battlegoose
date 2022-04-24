package se.battlegoo.battlegoose.controllers

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import se.battlegoo.battlegoose.network.MultiplayerService
import se.battlegoo.battlegoose.network.RandomPairingStatus
import se.battlegoo.battlegoose.utils.Modal
import se.battlegoo.battlegoose.utils.ModalType
import se.battlegoo.battlegoose.views.QuickJoinView

class QuickJoinController(
    private val quickJoinView: QuickJoinView,
    val onReadyStartBattle: (userID: String, battleID: String, isHost: Boolean) -> Unit,
    val onClickMainMenu: () -> Unit,
    val stage: Stage
) : ControllerBase(quickJoinView) {

    private var shouldStartBattle = false
    private var wantToLeaveQueue = false
    private var canLeaveQueue = false
    private var successfullyLeftQueue = false

    private var battleData: BattleStateData? = null


    init {
        MultiplayerService.tryRequestOpponent({ status, leaveQueue ->
            setBattleData(status)
            quickJoinView.setStatus(status)
            shouldStartBattle = status is RandomPairingStatus.StartBattle
            canLeaveQueue =
                status is RandomPairingStatus.WaitingForOtherPlayer ||
                    status is RandomPairingStatus.WaitingInQueue
            if (wantToLeaveQueue && canLeaveQueue) {
                leaveQueue({ reason, throwable ->
                    Modal(
                        "Error leaving queue",
                        "There was an error leaving queue. Try again later. " +
                            "Reason $reason, $throwable",
                        ModalType.Error(),
                        stage
                    ).show()
                }) {
                    successfullyLeftQueue = true
                }
            }
        })
    }

    private fun setBattleData(status: RandomPairingStatus) {
        if (status is RandomPairingStatus.StartBattle) {
            battleData = BattleStateData(
                userID = status.playerID,
                battleID = status.battleID,
                isHost = status.isHost
            )
        }
    }


    fun goBack() {
        wantToLeaveQueue = true
    }

    private fun handleInput() {
        quickJoinView.registerInput()
    }


    override fun update(dt: Float) {
        val dataCpy = battleData
        handleInput()
        when {
            shouldStartBattle && dataCpy != null -> onReadyStartBattle(
                dataCpy.userID,
                dataCpy.battleID,
                dataCpy.isHost
            )
            shouldStartBattle && dataCpy == null -> Modal(
                "Error starting battle",
                "Could not retrieve data to start battle.",
                ModalType.Error(),
                stage
            )
            wantToLeaveQueue && canLeaveQueue && successfullyLeftQueue -> onClickMainMenu()
        }

    }

    override fun render(sb: SpriteBatch) {
        quickJoinView.render(sb)
    }

    override fun dispose() {
        quickJoinView.dispose()
    }
}

data class BattleStateData(val userID: String, val battleID: String, val isHost: Boolean)
