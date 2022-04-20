package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.views.JoinLobbyView

class JoinLobbyState : GameState() {

    var joinLobbyView = JoinLobbyView(
        this::goBack
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
        /*
        if (!joined) {
            // Check for valid jobby id and successful join
            MultiplayerService.tryJoinLobby(joinLobbyView.getJoinLobbyId()) {
                val logger = Logger("Join Lobby").error(it.toString())
            }
        }
         */
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
