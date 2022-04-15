package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.Game

class JoinLobbyState : GameState() {
    private val background = Texture("placeholder.png")

    private val title: BitmapFont = BitmapFont()
    private val titleText = "JOIN LOBBY"
    private val layoutTitle = GlyphLayout(title, titleText)

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
        sb.draw(background, 0f, 0f, Game.WIDTH.toFloat(), Game.HEIGHT.toFloat())

        title.data.setScale(5f)
        title.draw(
            sb, titleText, (Game.WIDTH / 2f) - (layoutTitle.width * 5f / 2f),
            (Game.HEIGHT * 0.9f) + layoutTitle.height * 3f
        )

        goBack.data.setScale(3f)
        goBack.draw(
            sb, goBackText, Game.WIDTH / 20f - (layoutGoBack.width / 3f),
            Game.HEIGHT / 20f + layoutGoBack.height * 3f
        )
    }

    override fun dispose() {
        background.dispose()
        title.dispose()
        goBack.dispose()
    }
}
