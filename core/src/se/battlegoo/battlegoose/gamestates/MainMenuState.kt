package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.models.heroes.AdmiralAlbatross
import se.battlegoo.battlegoose.models.heroes.MajorMallard
import se.battlegoo.battlegoose.models.heroes.SergeantSwan
import se.battlegoo.battlegoose.views.MainMenuView

class MainMenuState :
    GameState() {

    private var mainMenuView = MainMenuView(
        onClickCreateLobby = { GameStateManager.push(CreateLobbyState()) },
//        onClickJoinLobby = { GameStateManager.push(JoinLobbyState()) },
        onClickJoinLobby = {
            GameStateManager.push(
                HeroSelectionState(
                    arrayOf(SergeantSwan(), MajorMallard(), AdmiralAlbatross())
                )
            )
        },
//        onClickQuickJoin = { GameStateManager.push(QuickJoinState()) },
        onClickQuickJoin = { GameStateManager.push(BattleState()) },
        onClickLeaderboard = { GameStateManager.push(LeaderboardState()) }
    )

    override fun update(dt: Float) {
        mainMenuView.registerInput()
    }

    override fun render(sb: SpriteBatch) {
        mainMenuView.render(sb)
    }

    override fun dispose() {
        mainMenuView.dispose()
    }
}
