package se.battlegoo.battlegoose.network

import com.badlogic.gdx.utils.Logger
import java.util.LinkedList
import java.util.function.Consumer
import pl.mk5.gdx.fireapp.promises.ListenerPromise
import pl.mk5.gdx.fireapp.promises.Promise
import se.battlegoo.battlegoose.datamodels.ActionData
import se.battlegoo.battlegoose.datamodels.BattleData
import se.battlegoo.battlegoose.datamodels.LobbyData

object MultiplayerService {
    private val databaseHandler = DatabaseHandler()
    private var battleID: String? = null

    private var lastReadActionIndex: Int = -1
    private var actionListBuffer: List<ActionData>? = null

    private var battleListeners = mutableListOf<ListenerPromise<*>>()

    init {
        databaseHandler.signInAnonymously()
    }

    private fun userCanJoinLobby(userID: String, lobbyData: LobbyData): Boolean {
        return lobbyData.otherPlayerID.isNotEmpty() && lobbyData.otherPlayerID != userID
    }

    private fun joinLobby(lobbyID: String, userID: String): Promise<Void> {
        return databaseHandler.setValue(
            "${DataPaths.LOBBIES}/$lobbyID/${LobbyData::otherPlayerID.name}",
            userID
        )
    }

    private fun generateReadableUID(): String {
        return (1..6).map { ('A'..'Z').random() }.joinToString("")
    }

    fun tryCreateLobby(listener: Consumer<LobbyData>) {
        databaseHandler.getUserID { userID ->
            val lobbyID = generateReadableUID()
            databaseHandler.setValue("${DataPaths.LOBBIES}/$lobbyID", LobbyData(lobbyID, userID)).then<Void> {
                listener.accept(LobbyData(lobbyID, userID))
            }
        }
    }

    fun tryRequestOpponent(listener: Consumer<Boolean>) {
        databaseHandler.getUserID { userID ->
            databaseHandler.readPrimitiveValue<Any>(
                DataPaths.RANDOM_OPPONENT_QUEUE.toString()
            ) { data ->
                val queue: LinkedList<String> =
                    if (data !is List<*>) {
                        LinkedList()
                    } else {
                        LinkedList(data.map { it as String })
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

    fun tryJoinLobby(lobbyID: String, listener: Consumer<LobbyStatus>) {
        databaseHandler.getUserID { userID ->
            databaseHandler.readReferenceValue<LobbyData>(
                "${DataPaths.LOBBIES}/$lobbyID",
                Consumer { lobby ->
                    if (lobby == null) {
                        listener.accept(LobbyStatus.DoesNotExist)
                        return@Consumer
                    }

                    if (userCanJoinLobby(userID, lobby)) {
                        listener.accept(LobbyStatus.Full)
                        return@Consumer
                    }

                    joinLobby(lobbyID, userID).then<Void> {
                        joinBattle(lobbyID)
                        listener.accept(
                            LobbyStatus.Ready(LobbyData(lobbyID, lobby.hostID, userID, lobby.shouldStart))
                        )
                    }
                }
            )
        }
    }

    fun startBattle(lobbyID: String) {
        val battleID = generateReadableUID()
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
            ).then<Void> {
                databaseHandler.setValue(
                    "${DataPaths.LOBBIES}/$lobbyID/${LobbyData::battleID.name}",
                    battleID
                )
                // Listen for the other player to join the battle
                var otherPlayerIDListener: ListenerPromise<String>? = null
                otherPlayerIDListener = databaseHandler.listenPrimitiveValue(
                    "${DataPaths.BATTLES}/$battleID/${BattleData::otherPlayerID.name}"
                ) { otherPlayerID ->
                    if (otherPlayerID == "" || otherPlayerID == null) {
                        return@listenPrimitiveValue
                    }
                    this.battleID = battleID
                    databaseHandler.deleteValue("${DataPaths.LOBBIES}/$lobbyID")
                    otherPlayerIDListener?.cancel()
                    listenForActions(battleID)
                }
            }
        }
    }

    private fun joinBattle(lobbyID: String) {
        var battleIDListener: ListenerPromise<String>? = null
        battleIDListener = databaseHandler.listenPrimitiveValue(
            "${DataPaths.LOBBIES}/$lobbyID/${LobbyData::battleID.name}"
        ) { battleID ->
            if (battleID == null || battleID == "") {
                return@listenPrimitiveValue
            }

            databaseHandler.getUserID { userID ->
                databaseHandler.setValue(
                    "${DataPaths.BATTLES}/$battleID/${BattleData::otherPlayerID.name}",
                    userID
                ).then<Void> {
                    battleIDListener?.cancel()
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
                "${DataPaths.BATTLES}/$battleDataID/${BattleData::actions.name}",
                it.actions + listOf(action)
            )
        }
    }

    private fun listenForActions(battleID: String) {
        val actionListener = databaseHandler.listenListValue<ActionData>(
            "${DataPaths.BATTLES}/$battleID/${BattleData::actions.name}"
        ) { updatedActionData ->
            if (updatedActionData == null) return@listenListValue
            actionListBuffer = if (actionListBuffer == null || lastReadActionIndex == -1) {
                updatedActionData
            } else {
                updatedActionData.subList(lastReadActionIndex + 1, updatedActionData.size)
            }
        }
        battleListeners.add(actionListener as ListenerPromise<*>)
    }

    fun readActionDataBuffer(): List<ActionData>? {
        val bufferCpy = actionListBuffer ?: return null
        this.lastReadActionIndex += bufferCpy.size
        actionListBuffer = emptyList()
        return bufferCpy
    }

    private fun resetActionDataBuffer() {
        this.lastReadActionIndex = -1
        this.actionListBuffer = null
    }

    fun endBattle() {
        battleListeners.forEach { it.cancel() }
        resetActionDataBuffer()
        battleListeners = mutableListOf()
        battleID = null
    }
}
