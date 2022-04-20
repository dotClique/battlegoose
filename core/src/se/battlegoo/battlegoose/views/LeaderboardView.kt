package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import se.battlegoo.battlegoose.Game

class LeaderboardView(
    private val onClickMainMenu: () -> Unit
) : ViewBase() {

    private val background = Texture("menuBackground.jpg")

    private var stage = Stage(Game.viewPort)
    private val skin: Skin = Skin(Gdx.files.internal("star-soldier-ui.json"))
    private var leaderboard: Table = Table()
    private var topPlayers: MutableList<String> = mutableListOf(
        "Arne", "Per", "Gudrun",
        "Olga", "Ulvhild", "Tore",
        "Jalmar", "Lise", "Vidar", "Ernst"
    )

    private val mainMenuButton: TextButton = TextButton("Main Menu", skin)
    private val titleLabel: Label = Label("Leaderboard", skin)

    private val x0: Float = Menu.SPACER
    private val y0: Float = Menu.BOTTOM_SPACING

    init {
        Gdx.input.inputProcessor = stage
        stage.addActor(leaderboard)
        stage.addActor(mainMenuButton)

        leaderboard.pad(10f).defaults().expandX().space(4f)
        addPlayersToLeaderboard(topPlayers)
        leaderboard.setPosition(
            Game.WIDTH / 2f - leaderboard.width / 2f,
            Game.HEIGHT / 1.7f - leaderboard.height / 2f
        )

        mainMenuButton.width = Menu.BUTTON_WIDTH.toFloat()
        mainMenuButton.height *= 1.5f

        titleLabel.setAlignment(Align.center)
    }

    fun addPlayersToLeaderboard(topPlayers: MutableList<String>) {
        for (i in 0 until topPlayers.size) {
            leaderboard.row()
            var player = Label("${i + 1} ${topPlayers[i]}", skin)
            player.setAlignment(Align.center)
            leaderboard.add(player)
            // player.color = Color.OLIVE // consider changing color of labels
        }
    }

    override fun registerInput() {
        if (Gdx.input.justTouched() && mainMenuButton.isPressed) {
            onClickMainMenu()
        }
    }

    override fun render(sb: SpriteBatch) {
        sb.draw(background, 0f, 0f, Game.WIDTH, Game.HEIGHT)

        titleLabel.setFontScale(5f)
        titleLabel.setPosition(
            (Game.WIDTH / 2f) - titleLabel.width / 2f,
            Game.HEIGHT * 0.9f
        )
        titleLabel.draw(sb, 1f)

        mainMenuButton.setPosition(x0, y0)
        mainMenuButton.draw(sb, 1f)
        leaderboard.draw(sb, 1f)
    }

    override fun dispose() {
        background.dispose()
        stage.dispose()
        skin.dispose()
    }
}
