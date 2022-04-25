package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.controllers.JoinLobbyController
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.views.JoinLobbyView

class JoinLobbyState(selectedHero: Hero) : LobbyState(selectedHero) {

    private var joinLobbyController: JoinLobbyController = JoinLobbyController(
        selectedHero,
        joinLobbyView = JoinLobbyView(stage),
        onReadyStartBattle = {
            GameStateManager.replace(
                BattleState(
                    it.battle.otherPlayerID,
                    it.battle.battleID,
                    it.battle.hostHero.toHero(),
                    it.battle.otherHero!!.toHero(),
                    false
                )
            )
        },
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
