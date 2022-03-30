package se.battlegoo.battlegoose.views

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class MainMenuView(var cam: OrthographicCamera) : ViewBase() {

    companion object {
        const val X_OFFSET = 20f // x-axis offset for menu screen options
        const val Y_OFFSET = 14f // y-axis offset for menu screen options
        const val SPACER = 1.1f // spacer between menu screen option
        const val SCALE = 3f // scale of main menu buttons
    }

    private val background = Texture("menuBackgroundGoose.png")

    private val x0: Float = cam.viewportWidth / X_OFFSET
    private val y0: Float = cam.viewportHeight / Y_OFFSET

    // Button icons background by Icons8
    private val createLobbyBtn = ButtonView(
        cam, "createLobbyBtn.png",
        x0, y0, SCALE
    )
    private val joinLobbyBtn = ButtonView(
        cam, "joinLobbyBtn.png",
        x0 + createLobbyBtn.btnWidth * SPACER, y0, SCALE
    )
    private val quickJoinBtn = ButtonView(
        cam, "quickJoinBtn.png",
        x0 + 2 * createLobbyBtn.btnWidth * SPACER, y0, SCALE
    )
    private val leaderboardBtn = ButtonView(
        cam, "leaderboardBtn.png",
        x0 + 3 * createLobbyBtn.btnWidth * SPACER, y0, SCALE
    )

    fun handleInput(): Int {
        if (createLobbyBtn.isPressed()) return 0
        else if (joinLobbyBtn.isPressed()) return 1
        else if (quickJoinBtn.isPressed()) return 2
        else if (leaderboardBtn.isPressed()) return 3
        else return -1
    }

    override fun render(sb: SpriteBatch) {
        sb.projectionMatrix = cam.combined
        sb.begin()

        sb.draw(background, 0f, 0f, cam.viewportWidth, cam.viewportHeight)

        createLobbyBtn.render(sb)
        joinLobbyBtn.render(sb)
        quickJoinBtn.render(sb)
        leaderboardBtn.render(sb)

        sb.end()
    }

    override fun dispose() {
        background.dispose()
        createLobbyBtn.dispose()
        joinLobbyBtn.dispose()
        quickJoinBtn.dispose()
        leaderboardBtn.dispose()
    }
}
