package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.gamestates.MainMenuState

class MainMenuView(var cam: OrthographicCamera): ViewBase() {

    companion object {
        const val X_OFFSET = 20f  // x-axis offset for menu screen options
        const val Y_OFFSET = 14f  // y-axis offset for menu screen options
        const val SPACER = 1.1f  // spacer between menu screen option
    }

    private val background = Texture("menuBackgroundGoose.png")

    // Button icons background by Icons8
    private val createLobbyTexture = Texture("createLobbyBtn.png")
    private val createLobbyBtn = TextureRegion(createLobbyTexture)

    private val joinLobbyTexture = Texture("joinLobbyBtn.png")
    private val joinLobbyBtn = TextureRegion(joinLobbyTexture)

    private val quickJoinTexture = Texture("quickJoinBtn.png")
    private val quickJoinBtn = TextureRegion(quickJoinTexture)

    private val leaderboardTexture = Texture("leaderboardBtn.png")
    private val leaderboardBtn = TextureRegion(leaderboardTexture)

    // Scaling properties of the main menu buttons
    private val mainMenuBtnWidth = createLobbyBtn.regionWidth*3f
    private val mainMenuBtnHeight = createLobbyBtn.regionHeight*3f

    override fun render(sb: SpriteBatch) {
        sb.projectionMatrix = cam.combined
        sb.begin()

        sb.draw(background, 0f, 0f, cam.viewportWidth, cam.viewportHeight)

        sb.draw(createLobbyBtn, cam.viewportWidth/ X_OFFSET, cam.viewportHeight/ Y_OFFSET, mainMenuBtnWidth, mainMenuBtnHeight)
        sb.draw(joinLobbyBtn, cam.viewportWidth/ X_OFFSET + mainMenuBtnWidth* SPACER, cam.viewportHeight/ Y_OFFSET, mainMenuBtnWidth, mainMenuBtnHeight)
        sb.draw(quickJoinBtn, cam.viewportWidth/ X_OFFSET + 2*mainMenuBtnWidth* SPACER, cam.viewportHeight/ Y_OFFSET, mainMenuBtnWidth, mainMenuBtnHeight)
        sb.draw(leaderboardBtn, cam.viewportWidth/ X_OFFSET + 3*mainMenuBtnWidth* SPACER, cam.viewportHeight/ Y_OFFSET, mainMenuBtnWidth, mainMenuBtnHeight)

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
