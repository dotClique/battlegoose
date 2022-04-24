package se.battlegoo.battlegoose.controllers

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Logger
import se.battlegoo.battlegoose.network.JoinLobbyStatus
import se.battlegoo.battlegoose.network.MultiplayerService
import se.battlegoo.battlegoose.utils.Modal
import se.battlegoo.battlegoose.utils.ModalType
import se.battlegoo.battlegoose.views.JoinLobbyView

class JoinLobbyController(
    val joinLobbyView: JoinLobbyView,
    val onReadyStartBattle: (JoinLobbyStatus.StartBattle) -> Unit,
    val onClickMainMenu: () -> Unit,
    val stage: Stage
) : ControllerBase(joinLobbyView) {

    private var joinLobbyStatus: JoinLobbyStatus? = null
    private var joined = false
    private var startBattle = false
    private var listenForStartingBattle: Boolean? = true // null denotes canceled listener
    private var leaveLobby = false

    init {
        joinLobbyView.onClickJoinLobby = { lobbyID ->
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
        joinLobbyView.onClickMainMenu = onClickMainMenu
    }

    private fun goBack() {
        val status = joinLobbyStatus
        if (status is JoinLobbyStatus.StartBattle)
            return Logger("battlegoose").error("Cannot exit lobby after battle has started")
        else if (status is JoinLobbyStatus.Ready)
            MultiplayerService.leaveLobbyAsOtherPlayer(
                status.lobby.lobbyID,
                onFail = { string, throwable ->
                    Modal(
                        "Failed to leave lobby",
                        "Try again later. Error:$string, $throwable",
                        ModalType.Error {},
                        stage
                    ).show()
                },
                onLeftLobby = {
                    leaveLobby = true
                }
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
        return (!joined && leaveLobby) ||
            (joined && leaveLobby && listenForStartingBattle == null)
    }

    override fun update(dt: Float) {
        handleInput()
        startBattle = joinLobbyStatus is JoinLobbyStatus.StartBattle
        if (startBattle) {
            onReadyStartBattle(joinLobbyStatus as JoinLobbyStatus.StartBattle)
            return
        }

//        Logger("battlegoose").error("JoinLobbyStatus: $joinLobbyStatus")
        if (joined && joinLobbyStatus is JoinLobbyStatus.NotAccessible
        ) {
            listenForStartingBattle = false
            Modal(
                "Lobby deleted",
                "The joined lobby was deleted. Try another lobby.",
                ModalType.Info(),
                stage
            ).show()
        }

        joined = joinLobbyStatus is JoinLobbyStatus.Ready
        if (canAndWantToLeave()) {
            onClickMainMenu()
        }
    }

    override fun render(sb: SpriteBatch) {
        joinLobbyView.render(sb)
    }

    override fun dispose() {
        joinLobbyView.dispose()
    }
}
