package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.views.CreateLobbyView

class CreateLobbyState : GameState() {

    private var cam: OrthographicCamera = OrthographicCamera()

    init {
        cam.setToOrtho(false, Game.WIDTH, Game.HEIGHT)
    }

    private val createLobbyView: CreateLobbyView = CreateLobbyView()

    private val goBack: BitmapFont = BitmapFont()
    private val goBackText = "Press anywhere to return to main menu..."
    private val layoutGoBack = GlyphLayout(goBack, goBackText)

    private fun handleInput() {
        if (Gdx.input.justTouched())
            GameStateManager.push(MainMenuState())
    }

    override fun update(dt: Float) {
        handleInput()
    }

    override fun render(sb: SpriteBatch) {
        createLobbyView.render(sb)

        goBack.data.setScale(3f)
        goBack.draw(
            sb, goBackText, cam.viewportWidth / 20f - (layoutGoBack.width / 3f),
            cam.viewportHeight / 20f + layoutGoBack.height * 3f
        )
    }

    override fun dispose() {
        createLobbyView.dispose()
        goBack.dispose()
    }
}
