package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.TextureRegion
import se.battlegoo.battlegoose.Game

class MainMenuState :
    GameState() {

    protected var cam: OrthographicCamera = OrthographicCamera()

    init {
        cam.setToOrtho(false, Game.WIDTH.toFloat(), Game.HEIGHT.toFloat())
    }

    private val background = Texture("menuBackgroundNoGoose.png")

    // Button icons background by Icons8
    private val createLobbyTexture = Texture("createLobbyBtn.png")
    private val createLobbyBtn = TextureRegion(createLobbyTexture)

    private val joinLobbyTexture = Texture("joinLobbyBtn.png")
    private val joinLobbyBtn = TextureRegion(joinLobbyTexture)

    private val quickJoinTexture = Texture("quickJoinBtn.png")
    private val quickJoinBtn = TextureRegion(quickJoinTexture)

    private val leaderboardTexture = Texture("leaderboardBtn.png")
    private val leaderboardBtn = TextureRegion(leaderboardTexture)

    private val mainMenuBtnWidth = createLobbyBtn.regionWidth*3f
    private val mainMenuBtnHeight = createLobbyBtn.regionHeight*3f

    private fun handleInput() {
        if (Gdx.input.justTouched())
            GameStateManager.push(LeaderboardState())
    }

    override fun update(dt: Float) {
        handleInput()
    }

    override fun render(sb: SpriteBatch) {
        sb.projectionMatrix = cam.combined
        sb.begin()
        sb.draw(background, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        sb.draw(createLobbyBtn, cam.viewportWidth/20f, cam.viewportHeight/14f, mainMenuBtnWidth, mainMenuBtnHeight)
        sb.draw(joinLobbyBtn, cam.viewportWidth/20f + mainMenuBtnWidth*1.1f, cam.viewportHeight/14f, mainMenuBtnWidth, mainMenuBtnHeight)
        sb.draw(quickJoinBtn, cam.viewportWidth/20f + 2*mainMenuBtnWidth*1.1f, cam.viewportHeight/14f, mainMenuBtnWidth, mainMenuBtnHeight)
        sb.draw(leaderboardBtn, cam.viewportWidth/20f + 3*mainMenuBtnWidth*1.1f, cam.viewportHeight/14f, mainMenuBtnWidth, mainMenuBtnHeight)

        sb.end()
    }

    override fun dispose() {
        background.dispose()
        createLobbyTexture.dispose()
        joinLobbyTexture.dispose()
        quickJoinTexture.dispose()
        leaderboardTexture.dispose()
    }
}
