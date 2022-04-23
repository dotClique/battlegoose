package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.datamodels.BattleData
import se.battlegoo.battlegoose.network.CreateLobbyStatus
import se.battlegoo.battlegoose.network.MultiplayerService
import se.battlegoo.battlegoose.utils.Modal
import se.battlegoo.battlegoose.utils.ModalType
import se.battlegoo.battlegoose.views.CreateLobbyView

class CreateLobbyState : GameState() {

    private val createLobbyView: CreateLobbyView = CreateLobbyView(
        this::goBack,
        stage
    )

    private var readyToStartBattle = false
    private var battleData: BattleData? = null
    private var startBattle = false
    private var listenForOtherPlayer: Boolean? = true // null denotes canceled listener

    private var lobbyId: String? = null
        set(value) {
            field = value
            value?.let(createLobbyView::setGeneratedLobbyId)
        }

    init {
        MultiplayerService.createLobby { lobbyData, status, cancelListener ->
            if (listenForOtherPlayer == false) {
                cancelListener()
                Logger("battlegoose").error("Canceled listener")
                listenForOtherPlayer = null
                return@createLobby
            }
            lobbyId = lobbyData.lobbyID
            readyToStartBattle = status == CreateLobbyStatus.OTHER_PLAYER_JOINED
            createLobbyView.setStatus(status)

        }
    }

    private fun goBack() {
        val lobbyIDCpy = lobbyId
            ?: return Logger("battlegoose")
                .info("Cannot leave lobby before a lobby is created.")
        MultiplayerService.deleteLobby(lobbyIDCpy,
            fail = { str, t ->
                Modal(
                    "Error deleting lobby",
                    "Deleting lobby failed with $str, $t",
                    ModalType.Error(),
                    stage
                ).show()
            })
        listenForOtherPlayer = false
    }

    private fun handleInput() {
        createLobbyView.registerInput()
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
        handleInput()
        if (listenForOtherPlayer == null) {
            // The listener is then canceled and it is ready to go back.
            GameStateManager.goBack()
            return
        }
        if (readyToStartBattle) {
            createLobbyView.onClickStartBattle = ::startBattle
        } else {
            createLobbyView.onClickStartBattle = null
        }
        battleData?.let { GameStateManager.replace(BattleState(it.hostID, it.battleID, true)) }
    }

    override fun render(sb: SpriteBatch) {
        createLobbyView.render(sb)
    }

    override fun dispose() {
        createLobbyView.dispose()
    }
}
