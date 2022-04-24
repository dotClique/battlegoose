package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.controllers.QuickJoinController
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.views.QuickJoinView

class QuickJoinState(selectedHero: Hero) : LobbyState(selectedHero) {

    private val quickJoinController = QuickJoinController(
        quickJoinView = QuickJoinView(this::goBack, stage),
        onReadyStartBattle = { userID, battleID, isHost ->
            GameStateManager.replace(
                BattleState(
                    userID,
                    battleID,
                    isHost
                )
            )
        },
        onClickMainMenu = { GameStateManager.goBack() },
        stage = stage
    )

    private fun goBack(): Unit = quickJoinController.goBack()

    override fun update(dt: Float) {
        quickJoinController.update(dt)
    }

    override fun render(sb: SpriteBatch) {
        quickJoinController.render(sb)
    }

    override fun dispose() {
        quickJoinController.dispose()
    }
}
