package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import se.battlegoo.battlegoose.network.LeaderboardEntry
import se.battlegoo.battlegoose.network.MultiplayerService
import se.battlegoo.battlegoose.views.LeaderboardView
import se.battlegoo.battlegoose.views.Skins

class LeaderboardState : GameState() {

    companion object {
        private const val UNKNOWN_USERNAME = "???"
        private const val LEADERBOARD_SIZE = 10
    }

    private val leaderboardView = LeaderboardView(
        this::goBack,
        stage
    )

    private val skin: Skin = Skin(Gdx.files.internal(Skins.STAR_SOLDIER.filepath))

    private var leaderboard: List<LeaderboardEntry> = listOf()
        set(value) {
            field = value
        }

    private val leaderboardText: String
        get() {
            return leaderboard.take(LEADERBOARD_SIZE).joinToString("\n") { entry ->
                "${(entry.username ?: UNKNOWN_USERNAME)}: ${entry.score}"
            }
        }

    init {
        updateLeaderboard()
    }

    private fun goBack() {
        GameStateManager.goBack()
    }

    private fun updateLeaderboard() {
        MultiplayerService.getLeaderboard {
            it?.let {
                leaderboard = it
                leaderboardView.setLeaderboardText(leaderboardText)
            }
        }
    }

    private fun handleInput() {
        leaderboardView.registerInput()
    }

    override fun update(dt: Float) {
        handleInput()
    }

    override fun render(sb: SpriteBatch) {
        leaderboardView.render(sb)
    }

    override fun dispose() {
        stage.dispose()
        skin.dispose()
    }
}
