package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.views.CreateLobbyView

class CreateLobbyState : GameState() {

    private var cam: OrthographicCamera = OrthographicCamera()

    init {
        cam.setToOrtho(false, Game.WIDTH, Game.HEIGHT)
    }

    private val createLobbyView: CreateLobbyView = CreateLobbyView()

    /*
    private val title: BitmapFont = BitmapFont()
    private val titleText = "CREATE LOBBY"
    private val layoutTitle = GlyphLayout(title, titleText)

    private val goBack: BitmapFont = BitmapFont()
    private val goBackText = "Press anywhere to return to main menu..."
    private val layoutGoBack = GlyphLayout(goBack, goBackText)
     */

    private fun handleInput() {
        createLobbyView.handleInput()
    }

    override fun update(dt: Float) {
        handleInput()
    }

    override fun render(sb: SpriteBatch) {
        createLobbyView.render(sb)

        /*
        title.data.setScale(5f)
        title.draw(
            sb, titleText, (Game.WIDTH / 2f) - (layoutTitle.width * 5f / 2f),
            (Game.HEIGHT * 0.9f) + layoutTitle.height * 3f
        )
         */

        /*
        goBack.data.setScale(3f)
        goBack.draw(
            sb, goBackText, cam.viewportWidth / 20f - (layoutGoBack.width / 3f),
            cam.viewportHeight / 20f + layoutGoBack.height * 3f
        )
         */
    }

    override fun dispose() {
        createLobbyView.dispose()
        // title.dispose()
        // goBack.dispose()
    }
}
