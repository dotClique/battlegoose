package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Logger
import se.battlegoo.battlegoose.network.MultiplayerService
import se.battlegoo.battlegoose.network.RandomOpponentStatus
import se.battlegoo.battlegoose.views.CreateLobbyView

class CreateLobbyState : GameState() {


    private var waitingTimer: Float = 0f
    private val letterSpawnTime: Float = 1f
    private var letterCount: Int = 0

    private val createLobbyView: CreateLobbyView = CreateLobbyView(
        this::goBack,
        stage
    )

    private var createLobbyCompleted = false
    private var readyToStartBattle = false
    private var startBattle = false

    private var lobbyId: String? = null
        set(value) {
            field = value
            value?.let(createLobbyView::setGeneratedLobbyId)
        }

    private fun goBack() {
        // TODO: Handle that player that created the lobby leaves lobby
        // TODO: Delete lobby
        GameStateManager.goBack()
    }

    private fun handleInput() {
        createLobbyView.registerInput()
        if (!createLobbyCompleted) {
            createLobbyCompleted = true
            MultiplayerService.tryCreateLobby { lobbyData ->
                Logger("Created lobby", Logger.INFO).info(lobbyData.toString())
                lobbyId = lobbyData.lobbyID
                MultiplayerService.listenForOtherPlayerJoinLobby(lobbyData.lobbyID) { status ->
                    readyToStartBattle = status == RandomOpponentStatus.OTHER_PLAYER_JOINED
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
        waitingTimer += dt
        if (letterCount >= 4f) {
            createLobbyView.resetWaitingText()
            letterCount = 0
        } else if (waitingTimer > letterSpawnTime) {
            createLobbyView.updateWaitingText()
            waitingTimer -= letterSpawnTime
            letterCount++
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
