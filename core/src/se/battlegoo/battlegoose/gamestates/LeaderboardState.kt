package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.network.LeaderboardEntry
import se.battlegoo.battlegoose.network.MultiplayerService
import se.battlegoo.battlegoose.views.Fonts
import se.battlegoo.battlegoose.views.Skins

class LeaderboardState : GameState() {

    companion object {
        private const val UNKNOWN_USERNAME = "???"
        private const val LEADERBOARD_SIZE = 10
    }

    private val background = Texture("placeholder.png")
    private val skin: Skin = Skin(Gdx.files.internal(Skins.STAR_SOLDIER.filepath))

    private val title: BitmapFont = BitmapFont()
    private val titleText = "LEADERBOARD"
    private val layoutTitle = GlyphLayout(title, titleText)

    private val goBack: BitmapFont = BitmapFont()
    private val goBackText = "Press anywhere to return to main menu..."
    private val layoutGoBack = GlyphLayout(goBack, goBackText)

    private var leaderboard: List<LeaderboardEntry> = listOf()
        set(value) {
            field = value
            leaderboardLayout.setText(leaderboardFont, leaderboardText)
        }

    private val leaderboardText: String
        get() {
            return leaderboard.take(LEADERBOARD_SIZE).joinToString("\n") { entry ->
                "${(entry.username ?: UNKNOWN_USERNAME)}: ${entry.score}"
            }
        }

    private val leaderboardFont: BitmapFont = skin.getFont(Fonts.STAR_SOLDIER.identifier)
    private val leaderboardLayout = GlyphLayout(leaderboardFont, leaderboardText)

    init {
        updateLeaderboard()
        leaderboardFont.data.setScale(3f)
    }

    private fun updateLeaderboard() {
        MultiplayerService.getLeaderboard { it?.let { leaderboard = it } }
    }

    private fun handleInput() {
        if (Gdx.input.justTouched()) {
            // GameStateManager.push(MainMenuState())
            MultiplayerService.incrementScore(1) { updateLeaderboard() }
        }
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

        leaderboardFont.draw(
            sb, leaderboardLayout, Game.WIDTH / 2f - leaderboardLayout.width / 2f,
            Game.HEIGHT * 0.8f
        )
    }

    override fun dispose() {
        background.dispose()
        title.dispose()
        goBack.dispose()
        leaderboardFont.dispose()
        skin.dispose()
    }
}
