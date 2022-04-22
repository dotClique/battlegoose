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

    private var joinLobbyView: JoinLobbyView? = null
    private var cancelStartBattleListener: ListenerCanceler = {}

    init {
        joinLobbyView = JoinLobbyView(
            onClickMainMenu = this::goBack,
            stage = stage,
            onJoinLobby = { lobbyID ->
                MultiplayerService.tryJoinLobby(lobbyID, {
                    joinLobbyStatus = it
                    joinLobbyView?.updateStatusLabel(it.message)
                },
                    { listenerCanceler ->
                        this.cancelStartBattleListener = {
                            Logger("ulrik").error("Canceled StartBattleListener")
                            listenerCanceler()
                        }
                    })
            }
        )
    }

    private var joinLobbyStatus: JoinLobbyStatus? = null
    private var waitingTimer: Float = 0f
    private val letterSpawnTime: Float = 1f
    private var letterCount: Int = 0

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
                        ModalType.Error {}).show()
                })
        cancelStartBattleListener()
        GameStateManager.goBack()
    }

    private fun handleInput() {
        joinLobbyView?.registerInput()
        joinLobbyView?.handleInput()
    }

    override fun update(dt: Float) {
        handleInput()
        joined = joinLobbyStatus is JoinLobbyStatus.Ready
        startBattle = joinLobbyStatus is JoinLobbyStatus.StartBattle
        // Dynamic 'waiting for opponent' message
        if (joined) {
            waitingTimer += dt
            if (letterCount >= 4) {
                joinLobbyView?.setStatusWaiting()
                letterCount = 0
            } else if (waitingTimer > letterSpawnTime) {
                joinLobbyView?.updateStatusWaiting()
                waitingTimer -= letterSpawnTime
                letterCount++
            }
        } else if (startBattle) {
            GameStateManager.push(BattleState())
        }
    }

    override fun render(sb: SpriteBatch) {
        joinLobbyView?.render(sb)
    }

    override fun dispose() {
        joinLobbyView?.dispose()
    }
}
