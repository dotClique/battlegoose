package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.network.LeaderboardEntry
import se.battlegoo.battlegoose.network.MultiplayerService
import se.battlegoo.battlegoose.views.screens.LeaderboardView

class LeaderboardState : GameState() {

    companion object {
        private const val UNKNOWN_USERNAME = "???"
        private const val LEADERBOARD_SIZE = 10
    }

    private val leaderboardView = LeaderboardView(
        this::goBack,
        stage
    )

    private var userId: String? = null

    private var leaderboard: List<LeaderboardEntry> = listOf()

    private fun getUserScoreText(): String {
        if (userId != null) {
            val place = leaderboard.indexOfFirst { it.userId == userId }
            if (place == -1) {
                MultiplayerService.incrementScore(0) {
                    updateLeaderboard()
                }
                return ""
            }
            val score = leaderboard[place].score
            return "\n\nYou (no. ${place + 1}): $score"
        }
        return ""
    }

    private fun getLeaderboardText(): String {
        return leaderboard.take(LEADERBOARD_SIZE).joinToString("\n") { entry ->
            "${(entry.username ?: UNKNOWN_USERNAME)}: ${entry.score}"
        }
    }

    init {
        updateLeaderboard()
        MultiplayerService.getUserID {
            userId = it
            updateText()
        }
    }

    private fun updateText() {
        leaderboardView.setLeaderboardText(getLeaderboardText() + getUserScoreText())
    }

    private fun goBack() {
        GameStateManager.goBack()
    }

    private fun updateLeaderboard() {
        MultiplayerService.getLeaderboard {
            it?.let {
                leaderboard = it
                updateText()
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
        leaderboardView.dispose()
    }
}
