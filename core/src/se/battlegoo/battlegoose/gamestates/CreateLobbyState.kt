package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Logger
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

    private var createLobbyCompleted = false
    private var readyToStartBattle = false
    private var startBattle = false

    private var cancelOtherPlayerIDListener: () -> Unit = {}
    private var lobbyId: String? = null
        set(value) {
            field = value
            value?.let(createLobbyView::setGeneratedLobbyId)
        }

    private fun goBack() {
        // TODO: Handle that player that created the lobby leaves lobby
        // TODO: Delete lobby
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
        if (!createLobbyCompleted) {
            createLobbyCompleted = true
            MultiplayerService.tryCreateLobby { lobbyData ->
                Logger("Created lobby", Logger.INFO).info(lobbyData.toString())
                lobbyId = lobbyData.lobbyID
                MultiplayerService.listenForOtherPlayerJoinLobby(
                    lobbyData.lobbyID
                ) { status, cancelListener ->
                    readyToStartBattle = status == CreateLobbyStatus.OTHER_PLAYER_JOINED
                    createLobbyView.setStatusText(status.message)
                    cancelOtherPlayerIDListener = cancelListener
                }
            }
        }
    }

    private fun startBattle() {
        val lobbyIDCpy =
            lobbyId ?: return Logger("ulrik").error(
                "LobbyID is null createLobbyState startBattle"
            )
        MultiplayerService.startBattle(lobbyIDCpy) {
            startBattle = true
        }
    }

    override fun update(dt: Float) {
        handleInput()
        Logger("ulrik").error("ReadyToStartBattle: $readyToStartBattle")
        // Dynamic 'waiting for opponent' message
        if (readyToStartBattle) {
            createLobbyView.onClickStartBattle = ::startBattle
        } else {
            createLobbyView.onClickStartBattle = null
        }
        if (startBattle) {
            GameStateManager.replace(BattleState())
        }
    }

    override fun render(sb: SpriteBatch) {
        createLobbyView.render(sb)
    }

    override fun dispose() {
        createLobbyView.dispose()
    }
}
