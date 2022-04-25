package se.battlegoo.battlegoose.controllers

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import se.battlegoo.battlegoose.datamodels.BattleData
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.network.MultiplayerService
import se.battlegoo.battlegoose.network.RandomPairingStatus
import se.battlegoo.battlegoose.utils.Modal
import se.battlegoo.battlegoose.utils.ModalType
import se.battlegoo.battlegoose.views.screens.QuickJoinView

class QuickJoinController(
    hero: Hero,
    private val quickJoinView: QuickJoinView,
    val onReadyStartBattle: (battle: BattleData, isHost: Boolean) -> Unit,
    val onClickMainMenu: () -> Unit,
    val stage: Stage
) : ControllerBase(quickJoinView) {

    companion object {
        private const val LEAVE_TIMEOUT = 2f
    }

    private var shouldStartBattle = false
    private var wantToLeaveQueue = false
    private var canLeaveQueue = false
    private var successfullyLeftQueue = false
    private var leaveTimeoutCounter = 0f

    private var battleData: RandomPairingStatus.StartBattle? = null

    private var showingErrorModal = false

    init {
        MultiplayerService.requestOpponent(hero, { status, leaveQueue ->
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
            battleData = status
        }
    }

    fun goBack() {
        wantToLeaveQueue = true
    }

    private fun showModal(modal: Modal) {
        if (!showingErrorModal) {
            modal.show()
            showingErrorModal = true
        }
    }

    private fun handleLeaveTimout(dt: Float) {
        if (wantToLeaveQueue && canLeaveQueue)
            leaveTimeoutCounter += dt
        if (leaveTimeoutCounter > LEAVE_TIMEOUT) {
            successfullyLeftQueue = true // Forcefully set this to true
        }
    }

    override fun update(dt: Float) {
        val dataCpy = battleData
        quickJoinView.registerInput()
        handleLeaveTimout(dt)
        when {
            wantToLeaveQueue && canLeaveQueue && successfullyLeftQueue -> onClickMainMenu()
            shouldStartBattle && dataCpy != null -> onReadyStartBattle(
                dataCpy.battle,
                dataCpy.isHost
            )
            shouldStartBattle && dataCpy == null -> showModal(
                Modal(
                    "Error starting battle",
                    "Could not retrieve data to start battle.",
                    ModalType.Error { showingErrorModal = false; onClickMainMenu() },
                    stage
                )

            )
        }
    }

    override fun render(sb: SpriteBatch) {
        quickJoinView.render(sb)
    }

    override fun dispose() {
        quickJoinView.dispose()
    }
}
