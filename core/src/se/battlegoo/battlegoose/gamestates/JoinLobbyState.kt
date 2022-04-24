package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Logger
import se.battlegoo.battlegoose.network.JoinLobbyStatus
import se.battlegoo.battlegoose.network.MultiplayerService
import se.battlegoo.battlegoose.utils.Modal
import se.battlegoo.battlegoose.utils.ModalType
import se.battlegoo.battlegoose.views.JoinLobbyView

class JoinLobbyState : GameState() {

    private val joinLobbyView: JoinLobbyView = createJoinLobbyView()

    private var joinLobbyStatus: JoinLobbyStatus? = null
    private var joined = false
    private var startBattle = false
    private var listenForStartingBattle: Boolean? = true // null denotes canceled listener
    private var leaveLobby = false

    private fun createJoinLobbyView(): JoinLobbyView {
        return JoinLobbyView(
                onClickMainMenu = this::goBack,
                stage = stage,
                onJoinLobby = { lobbyID ->
                    MultiplayerService.joinLobby(lobbyID) { status, cancelListener ->
                        if (listenForStartingBattle == false) {
                            listenForStartingBattle = null
                            cancelListener()
                            return@joinLobby
                        }
                        joinLobbyView.setStatus(status)
                        joinLobbyStatus = status
                    }
                }
        )
    }

    private fun goBack() {
        val status = joinLobbyStatus
        if (status is JoinLobbyStatus.StartBattle)
                return Logger("battlegoose").error("Cannot exit lobby after battle has started")
        else if (status is JoinLobbyStatus.Ready)
                MultiplayerService.leaveLobbyAsOtherPlayer(
                        status.lobby.lobbyID,
                        fail = { string, throwable ->
                            Modal(
                                            "Failed to leave lobby",
                                            "Try again later. Error:$string, $throwable",
                                            ModalType.Error {},
                                            stage
                                    )
                                    .show()
                        },
                        onLeftLobby = { leaveLobby = true }
                )
        else leaveLobby = true
        listenForStartingBattle = false
    }

    private fun handleInput() {
        joinLobbyView.registerInput()
    }

    private fun canAndWantToLeave(): Boolean {
        // You can leave if you have not joined a lobby and want to leave.
        // If you have joined a lobby and want to leave, you have to cancel
        // the listener for startingBattle first
        return (!joined && leaveLobby) || (joined && leaveLobby && listenForStartingBattle == null)
    }

    override fun update(dt: Float) {
        handleInput()

        // Go to BattleState if the host has started battle
        joinLobbyStatus?.let {
            if (it is JoinLobbyStatus.StartBattle) {
                GameStateManager.replace(
                        BattleState(it.lobby.otherPlayerID, it.lobby.battleID, false)
                )
            }
        }

        val newJoined = joinLobbyStatus is JoinLobbyStatus.Ready
        if (!newJoined && joined && joinLobbyStatus is JoinLobbyStatus.NotAccessible) {
            listenForStartingBattle = false
            Modal(
                            "Lobby deleted",
                            "The joined lobby was deleted. Try another lobby.",
                            ModalType.Info(),
                            stage
                    )
                    .show()
        }

        joined = newJoined
        if (canAndWantToLeave()) {
            GameStateManager.goBack()
        }
    }

    override fun render(sb: SpriteBatch) {
        joinLobbyView.render(sb)
    }

    override fun dispose() {
        joinLobbyView.dispose()
    }
}
