package se.battlegoo.battlegoose.views

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.Game

class MainMenuView : ViewBase() {

    companion object {
        const val BOTTOM_SPACING = 80f // y-axis offset for menu screen options
        const val SPACER = 50f // spacer between menu screen option

        // width of main menu buttons
        const val BUTTON_WIDTH = ((Game.WIDTH - 5 * SPACER) / 4).toInt()
    }

    private val background = Texture("menuBackgroundGoose.png")

    private val x0: Float = SPACER
    private val y0: Float = BOTTOM_SPACING

    // Button icons background by Icons8
    private val createLobbyBtn = ButtonView(
        "createLobbyBtn.png",
        x0, y0, BUTTON_WIDTH
    )
    private val joinLobbyBtn = ButtonView(
        "joinLobbyBtn.png",
        x0 + BUTTON_WIDTH + SPACER, y0, BUTTON_WIDTH
    )
    private val quickJoinBtn = ButtonView(
        "quickJoinBtn.png",
        x0 + 2 * (BUTTON_WIDTH + SPACER), y0, BUTTON_WIDTH
    )
    private val leaderboardBtn = ButtonView(
        "leaderboardBtn.png",
        x0 + 3 * (BUTTON_WIDTH + SPACER), y0, BUTTON_WIDTH
    )

    fun handleInput(): Int {
        if (createLobbyBtn.isPressed()) return 0
        else if (joinLobbyBtn.isPressed()) return 1
        else if (quickJoinBtn.isPressed()) return 2
        else if (leaderboardBtn.isPressed()) return 3
        else return -1
    }

    override fun render(sb: SpriteBatch) {
        sb.draw(background, 0f, 0f, Game.WIDTH.toFloat(), Game.HEIGHT.toFloat())

        createLobbyBtn.render(sb)
        joinLobbyBtn.render(sb)
        quickJoinBtn.render(sb)
        leaderboardBtn.render(sb)
    }

    override fun dispose() {
        background.dispose()
        createLobbyBtn.dispose()
        joinLobbyBtn.dispose()
        quickJoinBtn.dispose()
        leaderboardBtn.dispose()
    }
}
