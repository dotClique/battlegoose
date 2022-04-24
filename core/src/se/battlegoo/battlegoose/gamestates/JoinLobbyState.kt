package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Logger
import se.battlegoo.battlegoose.controllers.JoinLobbyController
import se.battlegoo.battlegoose.network.JoinLobbyStatus
import se.battlegoo.battlegoose.network.MultiplayerService
import se.battlegoo.battlegoose.utils.Modal
import se.battlegoo.battlegoose.utils.ModalType
import se.battlegoo.battlegoose.views.JoinLobbyView

class JoinLobbyState : GameState() {

    private var joinLobbyController: JoinLobbyController = JoinLobbyController(
        joinLobbyView = JoinLobbyView(stage),
        onReadyStartBattle = { GameStateManager.replace(BattleState(it.lobby.otherPlayerID, it.lobby.battleID, false)) },
        onClickMainMenu = { GameStateManager.goBack() },
        stage
    )

    override fun update(dt: Float) {
        joinLobbyController.update(dt)
    }

    override fun render(sb: SpriteBatch) {
        joinLobbyController.render(sb)
    }

    override fun dispose() {
        joinLobbyController.dispose()
    }
}
