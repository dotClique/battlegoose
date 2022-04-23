package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.utils.TextureAsset

class LeaderboardView(
    private val onClickMainMenu: () -> Unit,
    private val stage: Stage
) : ViewBase() {

    private val background = Game.getTexture(TextureAsset.MENU_BACKGROUND)

    private val skin: Skin = Skin(Gdx.files.internal(Skins.STAR_SOLDIER.filepath))

    private val mainMenuButton: TextButton = TextButton("Main Menu", skin)
    private val titleLabel: Label = Label("Leaderboard", skin)

    private val leaderboardFont: BitmapFont = skin.getFont(Fonts.STAR_SOLDIER.identifier)
    private val leaderboardLayout = GlyphLayout(leaderboardFont, "")

    private val x0: Float = Menu.SPACER
    private val y0: Float = Menu.BOTTOM_SPACING

    init {
        stage.addActor(mainMenuButton)

        mainMenuButton.width = Menu.BUTTON_WIDTH.toFloat()
        mainMenuButton.height *= 1.5f
        mainMenuButton.setPosition(x0, y0)

        leaderboardFont.data.setScale(3f)

        titleLabel.setAlignment(Align.center)
        titleLabel.setFontScale(5f)
        titleLabel.setPosition(
            (Game.WIDTH / 2f) - titleLabel.width / 2f,
            Game.HEIGHT * 0.9f
        )
    }

    fun setLeaderboardText(leaderboardText: String) {
        leaderboardLayout.setText(leaderboardFont, leaderboardText)
    }

    override fun registerInput() {
        if (Gdx.input.justTouched() && mainMenuButton.isPressed) {
            onClickMainMenu()
        }
    }

    override fun render(sb: SpriteBatch) {
        sb.draw(background, 0f, 0f, Game.WIDTH, Game.HEIGHT)

        titleLabel.draw(sb, 1f)

        mainMenuButton.draw(sb, 1f)

        leaderboardFont.draw(
            sb, leaderboardLayout, Game.WIDTH / 2f - leaderboardLayout.width / 2f,
            Game.HEIGHT * 0.8f
        )
    }

    override fun dispose() {
        skin.dispose()
        leaderboardFont.dispose()
    }
}
