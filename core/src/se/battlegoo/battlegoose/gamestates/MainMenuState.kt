package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.controllers.ChangeUsernameController
import se.battlegoo.battlegoose.views.ChangeUsernameView
import se.battlegoo.battlegoose.views.screens.MainMenuView

class MainMenuState :
    GameState() {

    private var mainMenuView = MainMenuView(
        stage,
        onClickCreateLobby = { GameStateManager.push(HeroSelectionState { CreateLobbyState(it) }) },
        onClickJoinLobby = { GameStateManager.push(HeroSelectionState { JoinLobbyState(it) }) },
//        onClickJoinLobby = {
//            Modal(
//                "A longer title than the other",
//                "This will do nothing ", ModalType.Info()).show()
//            Modal(
//                "Title",
//                "This will move page",
//                ModalType.Question(onYes = {GameStateManager.push(JoinLobbyState())} )).show()
//        },
        onClickQuickJoin = { GameStateManager.push(HeroSelectionState { QuickJoinState(it) }) },
        onClickLeaderboard = { GameStateManager.push(LeaderboardState()) },
        onClickTutorial = { GameStateManager.push(TutorialState()) }
    )

    private var changeUsernameController = ChangeUsernameController(
        ChangeUsernameView(
            Game.WIDTH - 700f, Game.HEIGHT - 100f,
            700f, 100f, stage = stage
        )
    )

    override fun update(dt: Float) {
        mainMenuView.registerInput()
        changeUsernameController.update(dt)
    }

    override fun render(sb: SpriteBatch) {
        mainMenuView.render(sb)
        changeUsernameController.render(sb)
    }

    override fun dispose() {
        mainMenuView.dispose()
        changeUsernameController.dispose()
    }
}
