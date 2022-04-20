package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Logger
import se.battlegoo.battlegoose.network.MultiplayerService
import se.battlegoo.battlegoose.views.CreateLobbyView

class CreateLobbyState : GameState() {

    private var waitingTimer: Float = 0f
    private val letterSpawnTime: Float = 1f
    private var letterCount: Int = 0

    private val createLobbyView: CreateLobbyView = CreateLobbyView(
        this::goBack
    )

    private var createLobbyCompleted = false

    private var lobbyId: String? = null

    private fun goBack() {
        GameStateManager.goBack()
    }

    private fun handleInput() {
        createLobbyView.registerInput()
        if (!createLobbyCompleted) {
            createLobbyCompleted = true
            MultiplayerService.tryCreateLobby() {
                val logger = Logger("Create Lobby").error(it.toString())
                lobbyId = it.lobbyID
                createLobbyView.setGeneratedLobbyId(lobbyId!!)
            }
        }
    }

    override fun update(dt: Float) {
        handleInput()
        // Dynamic 'waiting for opponent' message
        waitingTimer += dt
        if (letterCount >= 4f) {
            createLobbyView.resetWaitingText()
            letterCount = 0
        } else if (waitingTimer > letterSpawnTime) {
            createLobbyView.updateWaitingText()
            waitingTimer -= letterSpawnTime
            letterCount++
        }
    }

    override fun render(sb: SpriteBatch) {
        createLobbyView.render(sb)
    }

    override fun dispose() {
        createLobbyView.dispose()
    }
}
