package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.views.CreateLobbyView

class CreateLobbyState : GameState() {

    private var cam: OrthographicCamera = OrthographicCamera()

    private var timer: Float = 0f
    private val spawn: Float = 1f
    private var counter: Float = 0f

    init {
        cam.setToOrtho(false, Game.WIDTH, Game.HEIGHT)
    }

    private val createLobbyView: CreateLobbyView = CreateLobbyView()

    private fun handleInput() {
        createLobbyView.handleInput()
        if (createLobbyView.backToMainMenu()) {
            GameStateManager.push(MainMenuState())
        }
    }

    override fun update(dt: Float) {
        handleInput()
        // Dynamic 'waiting for opponent' message
        timer += dt
        if (counter >= 4f) {
            createLobbyView.resetWaitingText()
            counter -= 4f
        } else if (timer > spawn) {
            createLobbyView.updateWaitingText()
            timer -= spawn
            counter += 1f
        }
    }

    override fun render(sb: SpriteBatch) {
        createLobbyView.render(sb)
    }

    override fun dispose() {
        createLobbyView.dispose()
    }
}
