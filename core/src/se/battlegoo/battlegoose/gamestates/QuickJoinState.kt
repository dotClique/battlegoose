package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.network.MultiplayerService
import se.battlegoo.battlegoose.network.RandomPairingStatus
import se.battlegoo.battlegoose.utils.Modal
import se.battlegoo.battlegoose.utils.ModalType
import se.battlegoo.battlegoose.views.QuickJoinView

class QuickJoinState : GameState() {

    private var shouldStartBattle = false
    private var wantToLeaveQueue = false
    private var canLeaveQueue = false
    private var successfullyLeftQueue = false

    private val quickJoinView = QuickJoinView(
        this::goBack,
        stage
    )

    init {
        MultiplayerService.tryRequestOpponent({ status, leaveQueue ->
            quickJoinView.setStatus(status)
            shouldStartBattle = status == RandomPairingStatus.START_BATTLE
            canLeaveQueue = status == RandomPairingStatus.WAITING_FOR_OTHER_PLAYER ||
                status == RandomPairingStatus.WAITING_IN_QUEUE
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

    private fun goBack() {
        wantToLeaveQueue = true
    }

    private fun handleInput() {
        quickJoinView.registerInput()
    }

    override fun update(dt: Float) {
        handleInput()

        if (shouldStartBattle)
            GameStateManager.replace(BattleState())

        if (wantToLeaveQueue && canLeaveQueue && successfullyLeftQueue) {
            GameStateManager.goBack()
        }
    }

    override fun render(sb: SpriteBatch) {
        quickJoinView.render(sb)
    }

    override fun dispose() {
        quickJoinView.dispose()
    }
}
