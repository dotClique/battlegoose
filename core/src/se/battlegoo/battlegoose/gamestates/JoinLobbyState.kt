package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Logger
import se.battlegoo.battlegoose.network.JoinLobbyStatus
import se.battlegoo.battlegoose.network.ListenerCanceler
import se.battlegoo.battlegoose.network.MultiplayerService
import se.battlegoo.battlegoose.utils.Modal
import se.battlegoo.battlegoose.utils.ModalType
import se.battlegoo.battlegoose.views.JoinLobbyView

class JoinLobbyState : GameState() {

    private lateinit var joinLobbyView: JoinLobbyView
    private var cancelStartBattleListener: ListenerCanceler = {}

    init {
        joinLobbyView = JoinLobbyView(
            onClickMainMenu = this::goBack,
            stage = stage,
            onJoinLobby = { lobbyID ->
                MultiplayerService.tryJoinLobby(
                    lobbyID, {
                        joinLobbyStatus = it
                        joinLobbyView.setStatus(it)
                    },
                    { listenerCanceler ->
                        this.cancelStartBattleListener = {
                            Logger("ulrik").error("Canceled StartBattleListener")
                            listenerCanceler()
                        }
                    }
                )
            }
        )
    }

    private var joinLobbyStatus: JoinLobbyStatus? = null

    private var joined = false
    private var startBattle = false

    private fun goBack() {
        val status = joinLobbyStatus
        if (status is JoinLobbyStatus.StartBattle)
            return Logger("ulrik").error("Cannot exit lobby after battle has started")
        if (status is JoinLobbyStatus.Ready)
            MultiplayerService.leaveLobbyAsOtherPlayer(
                status.lobby.lobbyID,
                fail = { string, throwable ->
                    Modal(
                        "Failed to leave lobby",
                        "Try again later. Error:$string, $throwable",
                        ModalType.Error {},
                        stage
                    ).show()
                }
            )
        cancelStartBattleListener()
        GameStateManager.goBack()
    }

    private fun handleInput() {
        joinLobbyView.registerInput()
    }

    override fun update(dt: Float) {
        handleInput()
        val newJoined = joinLobbyStatus is JoinLobbyStatus.Ready
        if (!newJoined && joined && joinLobbyStatus is JoinLobbyStatus.NotAccessible) {

            cancelStartBattleListener()
            Modal(
                "Lobby deleted",
                "The joined lobby was deleted. Try another lobby.",
                ModalType.Info(),
                stage
            ).show()
        }

        joined = newJoined
        startBattle = joinLobbyStatus is JoinLobbyStatus.StartBattle

        if (startBattle) {
            GameStateManager.replace(BattleState())
        }
    }

    override fun render(sb: SpriteBatch) {
        joinLobbyView.render(sb)
    }

    override fun dispose() {
        joinLobbyView.dispose()
    }
}
