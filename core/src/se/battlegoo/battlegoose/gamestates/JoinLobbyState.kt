package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Logger
import se.battlegoo.battlegoose.network.MultiplayerService
import se.battlegoo.battlegoose.views.JoinLobbyView

class JoinLobbyState : GameState() {

    private val joinLobbyView = JoinLobbyView(
        // onClickMainMenu = this::goBack,
        stage = stage,
        onClickMainMenu = { GameStateManager.goBack() },
        onJoinLobby = { lobbyID ->
            MultiplayerService.tryJoinLobby(lobbyID) {
                Logger("Join Lobby status", Logger.INFO).info(it.toString())
            }
        }
    )

    private var waitingTimer: Float = 0f
    private val letterSpawnTime: Float = 1f
    private var letterCount: Int = 0

    private var joined = false

    private fun goBack() {
        GameStateManager.goBack()
    }

    private fun handleInput() {
        joinLobbyView.registerInput()
        joinLobbyView.handleInput()
    }

    override fun update(dt: Float) {
        handleInput()
        // Dynamic 'waiting for opponent' message
        if (joined) {
            waitingTimer += dt
            if (letterCount >= 4f) {
                joinLobbyView.resetWaitingText()
                letterCount = 0
            } else if (waitingTimer > letterSpawnTime) {
                joinLobbyView.updateWaitingText()
                waitingTimer -= letterSpawnTime
                letterCount++
            }
        }
    }

    override fun render(sb: SpriteBatch) {
        joinLobbyView.render(sb)
    }

    override fun dispose() {
        joinLobbyView.dispose()
    }
}
