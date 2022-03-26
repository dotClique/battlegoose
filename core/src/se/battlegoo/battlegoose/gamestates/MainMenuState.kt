package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.TextureRegion
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.views.MainMenuView

class MainMenuState :
    GameState() {

    private var cam: OrthographicCamera = OrthographicCamera()

    init {
        cam.setToOrtho(false, Game.WIDTH.toFloat(), Game.HEIGHT.toFloat())
    }

    private var mainMenuView = MainMenuView(cam)

    private fun handleInput() {
        if (Gdx.input.justTouched())
            GameStateManager.push(LeaderboardState())
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
