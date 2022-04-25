package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.controllers.CreateLobbyController
import se.battlegoo.battlegoose.views.CreateLobbyView

class CreateLobbyState : GameState() {

    private val createLobbyController: CreateLobbyController = CreateLobbyController(
        createLobbyView = CreateLobbyView(
            this::goBack,
            stage
        ),
        onClickStartBattle = {
            GameStateManager.replace(
                BattleState(
                    it.hostID,
                    it.battleID,
                    true
                )
            )
        },
        onClickMainMenu = { GameStateManager.goBack() },
        stage = stage
    )

    fun goBack() =
        createLobbyController.goBack()

    override fun update(dt: Float) {
        createLobbyController.update(dt)
    }

    override fun render(sb: SpriteBatch) {
        createLobbyController.render(sb)
    }

    override fun dispose() {
        createLobbyController.dispose()
    }
}
