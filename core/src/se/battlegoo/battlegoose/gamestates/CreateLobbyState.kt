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

    private var cancelOtherPlayerIDListener: () -> Unit = {}
    private var lobbyId: String? = null
        set(value) {
            field = value
            value?.let(createLobbyView::setGeneratedLobbyId)
        }

    init {
        MultiplayerService.tryCreateLobby { lobbyData ->
            lobbyId = lobbyData.lobbyID
            MultiplayerService.listenForOtherPlayerJoinLobby(
                lobbyData.lobbyID
            ) { status, cancelListener ->
                readyToStartBattle = status == CreateLobbyStatus.OTHER_PLAYER_JOINED
                createLobbyView.setStatus(status)
                cancelOtherPlayerIDListener = cancelListener
            }
        }
    }

    private fun goBack() {
        val lobbyIDCpy = lobbyId
        if (lobbyIDCpy != null)
            MultiplayerService.deleteLobby(lobbyIDCpy, fail = { str, t ->
                Modal(
                    "Error deleting lobby",
                    "Deleting lobby failed with $str, $t",
                    ModalType.Error(),
                    stage
                ).show()
            })
        cancelOtherPlayerIDListener()
        GameStateManager.goBack()
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
        // Dynamic 'waiting for opponent' message
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
