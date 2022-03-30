package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.views.MainMenuView

class MainMenuState :
    GameState() {

    private var mainMenuView = MainMenuView()

    private fun handleInput() {
        if (Gdx.input.justTouched()) {
            if (mainMenuView.handleInput() == 0) {
                GameStateManager.push(CreateLobbyState())
            } else if (mainMenuView.handleInput() == 1) {
                GameStateManager.push(JoinLobbyState())
            } else if (mainMenuView.handleInput() == 2) {
                GameStateManager.push(QuickJoinState())
            } else if (mainMenuView.handleInput() == 3) {
                GameStateManager.push(LeaderboardState())
            }
        }
    }

    override fun update(dt: Float) {
        handleInput()
    }

    override fun render(sb: SpriteBatch) {
        mainMenuView.render(sb)
    }

    override fun dispose() {
        mainMenuView.dispose()
    }
}
