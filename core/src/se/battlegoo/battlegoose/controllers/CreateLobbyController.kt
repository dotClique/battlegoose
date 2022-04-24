package se.battlegoo.battlegoose.controllers

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Logger
import se.battlegoo.battlegoose.datamodels.BattleData
import se.battlegoo.battlegoose.network.CreateLobbyStatus
import se.battlegoo.battlegoose.network.MultiplayerService
import se.battlegoo.battlegoose.utils.Modal
import se.battlegoo.battlegoose.utils.ModalType
import se.battlegoo.battlegoose.views.CreateLobbyView

class CreateLobbyController(
    val createLobbyView: CreateLobbyView,
    val onClickStartBattle: (BattleData) -> Unit,
    val onClickMainMenu: () -> Unit,
    val stage: Stage
) : ControllerBase(createLobbyView) {

    private var readyToStartBattle = false
    private var listenForOtherPlayer: Boolean? = true // null denotes canceled listener
    private var battleData: BattleData? = null

    private var lobbyId: String? = null
        set(value) {
            field = value
            value?.let(createLobbyView::setGeneratedLobbyId)
        }

    init {
        MultiplayerService.createLobby { lobbyData, status, cancelListener ->
            if (listenForOtherPlayer == false) {
                cancelListener()
                listenForOtherPlayer = null
                return@createLobby
            }
            lobbyId = lobbyData.lobbyID
            readyToStartBattle = status == CreateLobbyStatus.OTHER_PLAYER_JOINED
            createLobbyView.setStatus(status)
        }
    }

    fun goBack() {
        val lobbyIDCpy = lobbyId
            ?: return Logger("battlegoose")
                .info("Cannot leave lobby before a lobby is created.")
        MultiplayerService.deleteLobby(
            lobbyIDCpy,
            onFail = { str, t ->
                Modal(
                    "Error deleting lobby",
                    "Deleting lobby failed with $str, $t",
                    ModalType.Error(),
                    stage
                ).show()
            }
        )
        listenForOtherPlayer = false
    }

    private fun startBattle() {
        val lobbyIDCpy =
            lobbyId ?: return Modal(
                "Error starting battle",
                "There was no lobby to start battle from. Try again later.",
                ModalType.Error(),
                stage
            ).show()
        MultiplayerService.startBattle(lobbyIDCpy) {
            battleData = it
        }
    }

    override fun update(dt: Float) {
        createLobbyView.registerInput()
        val battleDataCpy = battleData
        if (battleDataCpy != null) {
            createLobbyView.onClickStartBattle = {}
            onClickStartBattle(battleDataCpy)
        } else if (listenForOtherPlayer == null) {
            onClickMainMenu()
        } else {
            if (readyToStartBattle) {
                createLobbyView.onClickStartBattle = ::startBattle
            } else {
                createLobbyView.onClickStartBattle = null
            }
        }
    }
}
