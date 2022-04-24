package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.utils.TextureAsset

class MainMenuView(
    stage: Stage,
    onClickCreateLobby: () -> Unit,
    onClickJoinLobby: () -> Unit,
    onClickQuickJoin: () -> Unit,
    onClickLeaderboard: () -> Unit,
    private val onClickTutorial: () -> Unit
) : ViewBase() {

    private val background = Game.getTexture(TextureAsset.MENU_BACKGROUND_GOOSE)
    private val skin = Skin(Gdx.files.internal(Skins.STAR_SOLDIER.filepath))

    private val x0: Float = Menu.SPACER
    private val y0: Float = Menu.BOTTOM_SPACING

    // Button icons background by Icons8
    private val createLobbyBtn = ButtonView(
        "createLobbyBtn.png",
        x0, y0, Menu.BUTTON_WIDTH, onClickCreateLobby
    )
    private val joinLobbyBtn = ButtonView(
        "joinLobbyBtn.png",
        x0 + Menu.BUTTON_WIDTH + Menu.SPACER, y0, Menu.BUTTON_WIDTH, onClickJoinLobby
    )
    private val quickJoinBtn = ButtonView(
        "quickJoinBtn.png",
        x0 + 2 * (Menu.BUTTON_WIDTH + Menu.SPACER), y0, Menu.BUTTON_WIDTH, onClickQuickJoin
    )
    private val leaderboardBtn = ButtonView(
        "leaderboardBtn.png",
        x0 + 3 * (Menu.BUTTON_WIDTH + Menu.SPACER), y0, Menu.BUTTON_WIDTH, onClickLeaderboard
    )
    private val tutorialBtn = TextButton("How to play", skin)

    init {
        tutorialBtn.setPosition(30f, 320f)
        tutorialBtn.setSize(500f, 120f)
        stage.addActor(tutorialBtn)
    }

    override fun registerInput() {
        if (Gdx.input.justTouched() && tutorialBtn.isPressed)
            onClickTutorial()
        else {
            createLobbyBtn.registerInput()
            joinLobbyBtn.registerInput()
            quickJoinBtn.registerInput()
            leaderboardBtn.registerInput()
        }
    }

    override fun render(sb: SpriteBatch) {
        sb.draw(background, 0f, 0f, Game.WIDTH, Game.HEIGHT)
        createLobbyBtn.render(sb)
        joinLobbyBtn.render(sb)
        quickJoinBtn.render(sb)
        leaderboardBtn.render(sb)
        tutorialBtn.draw(sb, 1f)
    }

    override fun dispose() {
        createLobbyBtn.dispose()
        joinLobbyBtn.dispose()
        quickJoinBtn.dispose()
        leaderboardBtn.dispose()
        skin.dispose()
    }
}
