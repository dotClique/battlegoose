package se.battlegoo.battlegoose.views.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.utils.TextureAsset
import se.battlegoo.battlegoose.views.utils.ButtonView
import se.battlegoo.battlegoose.views.utils.MenuLayout
import se.battlegoo.battlegoose.views.utils.Skins
import se.battlegoo.battlegoose.views.utils.ViewBase

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

    private val x0: Float = MenuLayout.SPACER
    private val y0: Float = MenuLayout.BOTTOM_SPACING

    // Button icons background by Icons8
    private val createLobbyBtn = ButtonView(
        "createLobbyBtn.png",
        x0, y0, MenuLayout.BUTTON_WIDTH, onClickCreateLobby
    )
    private val joinLobbyBtn = ButtonView(
        "joinLobbyBtn.png",
        x0 + MenuLayout.BUTTON_WIDTH + MenuLayout.SPACER,
        y0,
        MenuLayout.BUTTON_WIDTH, onClickJoinLobby
    )
    private val quickJoinBtn = ButtonView(
        "quickJoinBtn.png",
        x0 + 2 * (MenuLayout.BUTTON_WIDTH + MenuLayout.SPACER),
        y0,
        MenuLayout.BUTTON_WIDTH,
        onClickQuickJoin
    )
    private val leaderboardBtn = ButtonView(
        "leaderboardBtn.png",
        x0 + 3 * (MenuLayout.BUTTON_WIDTH + MenuLayout.SPACER),
        y0,
        MenuLayout.BUTTON_WIDTH,
        onClickLeaderboard
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
