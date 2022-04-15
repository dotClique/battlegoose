package se.battlegoo.battlegoose.network

import com.badlogic.gdx.utils.Logger
import pl.mk5.gdx.fireapp.promises.Promise
import se.battlegoo.battlegoose.datamodels.ActionData
import se.battlegoo.battlegoose.datamodels.BattleData
import se.battlegoo.battlegoose.datamodels.LobbyData
import java.util.LinkedList
import java.util.UUID
import java.util.function.Consumer

object MultiplayerService {
    private val databaseHandler = DatabaseHandler()
    private var battleID: String? = null

    private var lastReadActionIndex: Int = -1
    private var actionListBuffer: List<ActionData>? = null

    init {
        databaseHandler.signInAnonymously()
    }

    private fun userCanJoinLobby(userID: String, lobbyData: LobbyData): Boolean {
        return lobbyData.otherPlayerID.isNotEmpty() && lobbyData.otherPlayerID != userID
    }

    private fun joinLobby(lobbyID: String, userID: String): Promise<Void> {
        return databaseHandler.setValue("${DataPaths.LOBBIES}/$lobbyID/otherPlayerID", userID)
    }

    fun tryCreateLobby(listener: Consumer<LobbyData>) {
        databaseHandler.getUserID { userID ->
            databaseHandler.pushValue(DataPaths.LOBBIES.toString(), LobbyData(userID)).then<Void> {
                listener.accept(LobbyData(userID))
            }
        }
    }

    fun tryRequestOpponent(listener: Consumer<Boolean>) {
        databaseHandler.getUserID { userID ->
            databaseHandler.readPrimitiveValue<Any>(
                DataPaths.RANDOM_OPPONENT_QUEUE.toString()
            ) { data ->
                val queue: LinkedList<String> =
                    if (data !is ArrayList<*>) {
                        LinkedList()
                    } else {
                        LinkedList(data.map { it.toString() })
                    }

                if (!queue.contains(userID)) {
                    queue.add(userID)
                    databaseHandler.setValue(DataPaths.RANDOM_OPPONENT_QUEUE.toString(), queue)
                }

                if (queue.peek() == userID && queue.size > 1) {
                    // TODO: This user is now responsible for creating a lobby
                }
            }
        }
    }

    fun tryJoinLobby(lobbyID: String, listener: Consumer<Pair<LobbyStatus, LobbyData?>>) {
        databaseHandler.getUserID { userID ->
            databaseHandler.readReferenceValue<LobbyData>(
                "${DataPaths.LOBBIES}/$lobbyID",
                Consumer { lobby ->
                    if (lobby == null) {
                        listener.accept(Pair(LobbyStatus.DOES_NOT_EXIST, null))
                        return@Consumer
                    }

                    if (userCanJoinLobby(userID, lobby)) {
                        listener.accept(Pair(LobbyStatus.FULL, null))
                        return@Consumer
                    }

                    joinLobby(lobbyID, userID).then<Void> {
                        joinBattle(lobbyID)
                        listener.accept(
                            Pair(
                                LobbyStatus.READY,
                                LobbyData(lobby.hostID, userID, lobby.shouldStart)
                            )
                        )
                    }
                }
            )
        }
    }

    fun startBattle(lobbyID: String) {
        val battleID = UUID.randomUUID().toString()
        databaseHandler.getUserID { userID ->
            val initialBattleData =
                BattleData(
                    battleID,
                    userID,
                    "",
                    listOf()
                ) // The otherPlayerId is set by that other player.

            databaseHandler.setValue(
                "${DataPaths.BATTLES}/$battleID", initialBattleData
            ).then<
                Void> {
                databaseHandler.setValue("${DataPaths.LOBBIES}/$lobbyID/battleID", battleID)
                // Listen for the other player to join the battle
                var hasDeletedLobby = false
                databaseHandler.listenPrimitiveValue<String>(
                    "${DataPaths.BATTLES}/$battleID/otherPlayerID"
                ) { otherPlayerID ->
                    if (otherPlayerID == null || otherPlayerID.length < 0 || hasDeletedLobby) {
                        return@listenPrimitiveValue
                    }
                    hasDeletedLobby = true
                    this.battleID = battleID
                    databaseHandler.deleteValue("${DataPaths.LOBBIES}/$lobbyID")
                    listenForActions(battleID)
                }
            }
        }
    }

    private fun joinBattle(lobbyID: String) {
        var hasJoinedBattle = false
        databaseHandler.listenPrimitiveValue<String>(
            "${DataPaths.LOBBIES}/$lobbyID/battleID"
        ) { battleID ->
            if (hasJoinedBattle || battleID == null || battleID == "") {
                return@listenPrimitiveValue
            }

            databaseHandler.getUserID { userID ->
                databaseHandler.setValue("${DataPaths.BATTLES}/$battleID/otherPlayerID", userID)
                    .then<Void> {
                        hasJoinedBattle = true
                    }
            }

            this.battleID = battleID
            listenForActions(battleID)
        }
    }

    /** Pushes an action to the actionlist. */
    fun postAction(action: ActionData) {
        val battleDataID =
            battleID ?: return Logger("ulrik").error("No battleID") // TODO: Handle error
        databaseHandler.readReferenceValue<BattleData>(
            "${DataPaths.BATTLES}/$battleDataID"
        ) Consumer@{
            if (it == null)
                return@Consumer Logger("ulrik").error("BattleData is null") // TODO: Error
            databaseHandler.setValue(
                "${DataPaths.BATTLES}/$battleDataID/actions",
                it.actions + listOf(action)
            )
        }
    }

    private fun listenForActions(battleID: String) {
        databaseHandler.listenListValue<ActionData>(
            "${DataPaths.BATTLES}/$battleID/actions"
        ) { updatedActionData ->
            val lastReadIndex = lastReadActionIndex
            val bufferCpy = actionListBuffer
            if (updatedActionData == null) return@listenListValue
            if (bufferCpy == null || lastReadIndex == -1) {
                actionListBuffer = updatedActionData
            } else {
                actionListBuffer =
                    updatedActionData.subList(lastReadIndex + 1, updatedActionData.size)
            }
        }
    }

    fun readActionDataBuffer(): List<ActionData>? {
        val bufferCpy = actionListBuffer ?: return null
        this.lastReadActionIndex += bufferCpy.size
        actionListBuffer = emptyList()
        return bufferCpy
    }

    fun resetActionDataBuffer() {
        this.lastReadActionIndex = -1
        this.actionListBuffer = null
    }
}
