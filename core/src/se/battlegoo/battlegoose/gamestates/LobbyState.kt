package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.controllers.CreateLobbyController
import se.battlegoo.battlegoose.controllers.JoinLobbyController
import se.battlegoo.battlegoose.controllers.QuickJoinController
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.views.CreateLobbyView
import se.battlegoo.battlegoose.views.screens.JoinLobbyView
import se.battlegoo.battlegoose.views.screens.QuickJoinView

abstract class LobbyState(val selectedHero: Hero) : GameState()

class CreateLobbyState(selectedHero: Hero) : LobbyState(selectedHero) {

    private val createLobbyController: CreateLobbyController = CreateLobbyController(
        selectedHero,
        createLobbyView = CreateLobbyView(
            this::goBack,
            stage
        ),
        onClickStartBattle = {
            GameStateManager.replace(
                BattleState(
                    it.hostID,
                    it.battleID,
                    it.hostHero.toHero(),
                    it.otherHero!!.toHero(),
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

class QuickJoinState(selectedHero: Hero) : LobbyState(selectedHero) {

    private val quickJoinController = QuickJoinController(
        selectedHero,
        quickJoinView = QuickJoinView(this::goBack, stage),
        onReadyStartBattle = { battle, isHost ->
            GameStateManager.replace(
                BattleState(
                    if (isHost) battle.hostID else battle.otherPlayerID,
                    battle.battleID,
                    battle.hostHero.toHero(),
                    battle.otherHero!!.toHero(),
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
