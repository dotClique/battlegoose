package se.battlegoo.battlegoose.network

import com.badlogic.gdx.utils.Logger
import se.battlegoo.battlegoose.datamodels.ActionData
import se.battlegoo.battlegoose.datamodels.BattleData
import se.battlegoo.battlegoose.datamodels.LobbyData
import se.battlegoo.battlegoose.datamodels.RandomOpponentData
import java.util.Date
import java.util.LinkedList
import java.util.function.Consumer

object MultiplayerService {
    private val databaseHandler = DatabaseHandler()
    private var battleID: String? = null

    private var lastReadActionIndex: Int = -1
    private var actionListBuffer: List<ActionData>? = null

    private var battleListenerCancelers = mutableListOf<ListenerCanceler>()

    private fun userCanJoinLobby(userID: String, lobbyData: LobbyData): Boolean {
        return lobbyData.otherPlayerID.isNotEmpty() && lobbyData.otherPlayerID != userID
    }

    private fun joinLobby(lobbyID: String, userID: String, callback: () -> Unit) {
        databaseHandler.setValue(
            "${DataPaths.LOBBIES}/$lobbyID/${LobbyData::otherPlayerID.name}",
            userID,
            callback = callback
        )
    }

    private fun generateReadableUID(): String {
        return (1..6).map { ('A'..'Z').random() }.joinToString("")
    }

    fun tryCreateLobby(listener: Consumer<LobbyData>) {
        databaseHandler.getUserID { userID ->
            val lobbyID = generateReadableUID()
            databaseHandler.setValue(
                "${DataPaths.LOBBIES}/$lobbyID", LobbyData(lobbyID, userID)
            ) {
                listener.accept(LobbyData(lobbyID, userID))
            }
        }
    }

    private fun checkRandomLobbyAvailability(consumer: Consumer<String?>) =
        databaseHandler.readPrimitiveValue<String>(
            "${DataPaths.RANDOM_OPPONENT_DATA}/availableLobby"
        ) { lobbyID -> consumer.accept(lobbyID) }

    private fun purgeInactiveFromQueue(
        listener: Consumer<RandomOpponentStatus>,
        purgedQueueConsumer: Consumer<Any?>
    ) = databaseHandler.readPrimitiveValue<Long>(
        "${DataPaths.RANDOM_OPPONENT_DATA}/lastUpdated"
    ) { lastUpdated ->
        val now = Date().time
        val timeoutMs = 10000

        if (lastUpdated != null && now - lastUpdated < timeoutMs) {
            purgedQueueConsumer.accept(null)
            return@readPrimitiveValue
        }

        databaseHandler.setValue("${DataPaths.RANDOM_OPPONENT_DATA}/lastUpdated", now) {
            databaseHandler.setValue(
                "${DataPaths.RANDOM_OPPONENT_QUEUE}", LinkedList<String>()
            ) {
                listener.accept(RandomOpponentStatus.TIMEOUT_INACTIVE_PLAYER)
                purgedQueueConsumer.accept(LinkedList<String>())
            }
        }
    }

    private fun getQueueFromQueueData(queueData: Any?): LinkedList<String> =
        if (queueData !is List<*>)
            LinkedList()
        else
            LinkedList(queueData.map { it as String })

    private fun addPlayerToQueue(
        userID: String,
        queue: LinkedList<String>,
        listener: Consumer<RandomOpponentStatus>
    ) {
        if (queue.contains(userID)) return

        queue.add(userID)
        databaseHandler.setValue(DataPaths.RANDOM_OPPONENT_QUEUE.toString(), queue) {
            listener.accept(RandomOpponentStatus.JOIN_QUEUE)
        }
    }

    private fun listenForOtherPlayerJoinLobby(
        lobbyID: String,
        listener: Consumer<RandomOpponentStatus>
    ) {
        databaseHandler.listenPrimitiveValue<String>(
            "${DataPaths.LOBBIES}/$lobbyID/${LobbyData::otherPlayerID}"
        ) { otherPlayerID, otherPlayerIDListenerCanceler ->
            if (!otherPlayerID.isNullOrEmpty()) {
                otherPlayerIDListenerCanceler.invoke()
                listener.accept(RandomOpponentStatus.OTHER_PLAYER_JOINED)
                return@listenPrimitiveValue
            }
            listener.accept(RandomOpponentStatus.WAITING_FOR_OTHER_PLAYER)
        }
    }

    private fun setAvailableLobby(availableLobby: String, callback: () -> Unit) {
        databaseHandler.setValue(
            "${DataPaths.RANDOM_OPPONENT_DATA}",
            RandomOpponentData(availableLobby, Date().time),
            callback = callback
        )
    }

    private fun popQueue(queue: LinkedList<String>, callback: () -> Unit) {
        queue.pop()
        databaseHandler.setValue(
            DataPaths.RANDOM_OPPONENT_QUEUE.toString(),
            queue,
            callback = callback
        )
    }

    fun tryRequestOpponent(listener: Consumer<RandomOpponentStatus>) {
        var processing = false
        databaseHandler.listenPrimitiveValue<Any>(
            DataPaths.RANDOM_OPPONENT_QUEUE.toString(),
        ) { updatedQueueData, queueListenerCanceler ->
            if (processing) {
                return@listenPrimitiveValue
            }
            processing = true

            purgeInactiveFromQueue(listener) { purgedQueue ->
                val queue = getQueueFromQueueData(purgedQueue ?: updatedQueueData)

                databaseHandler.getUserID { userID ->
                    addPlayerToQueue(userID, queue, listener)

                    if (queue.peek() != userID) {
                        processing = false
                        listener.accept(RandomOpponentStatus.WAITING_IN_QUEUE)
                        return@getUserID
                    }

                    this.checkRandomLobbyAvailability { availableLobbyID ->

                        if (!availableLobbyID.isNullOrEmpty()) {
                            setAvailableLobby("") {
                                tryJoinLobby(availableLobbyID) {
                                    queueListenerCanceler.invoke()
                                    popQueue(queue) {
                                        listener.accept(RandomOpponentStatus.JOINED_LOBBY)
                                    }
                                }
                            }
                            return@checkRandomLobbyAvailability
                        }

                        if (queue.size > 1) {
                            tryCreateLobby { lobby ->
                                setAvailableLobby(lobby.lobbyID) {
                                    listenForOtherPlayerJoinLobby(lobby.lobbyID, listener)
                                    queueListenerCanceler.invoke()
                                    popQueue(queue) {
                                        listener.accept(RandomOpponentStatus.CREATED_LOBBY)
                                    }
                                }
                            }
                            return@checkRandomLobbyAvailability
                        }

                        processing = false
                        listener.accept(RandomOpponentStatus.WAITING_FOR_OTHER_PLAYER)
                    }
                }
            }
        }
    }

    fun tryJoinLobby(lobbyID: String, listener: Consumer<LobbyStatus>) {
        databaseHandler.getUserID { userID ->
            databaseHandler.readReferenceValue<LobbyData>(
                "${DataPaths.LOBBIES}/$lobbyID",
                consumer = { lobby ->
                    if (lobby == null) {
                        listener.accept(LobbyStatus.DoesNotExist)
                        return@readReferenceValue
                    }

                    if (userCanJoinLobby(userID, lobby)) {
                        listener.accept(LobbyStatus.Full)
                        return@readReferenceValue
                    }

                    joinLobby(lobbyID, userID) {
                        joinBattle(lobbyID)
                        listener.accept(
                            LobbyStatus.Ready(
                                LobbyData(
                                    lobbyID,
                                    lobby.hostID,
                                    userID,
                                    lobby.shouldStart
                                )
                            )
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
            ) {
                databaseHandler.setValue(
                    "${DataPaths.LOBBIES}/$lobbyID/${LobbyData::battleID.name}",
                    battleID
                ) {
                    // Listen for the other player to join the battle
                    databaseHandler.listenPrimitiveValue<String>(
                        "${DataPaths.BATTLES}/$battleID/${BattleData::otherPlayerID.name}"
                    ) { otherPlayerID, otherPlayerIDListenerCanceler ->
                        if (otherPlayerID == null || otherPlayerID.length < 0) {
                            return@listenPrimitiveValue
                        }
                        this.battleID = battleID
                        databaseHandler.deleteValue("${DataPaths.LOBBIES}/$lobbyID") {}
                        otherPlayerIDListenerCanceler.invoke()
                        listenForActions(battleID)
                    }
                }
            }
        }
    }

    private fun joinBattle(lobbyID: String) {
        databaseHandler.listenPrimitiveValue<String>(
            "${DataPaths.LOBBIES}/$lobbyID/${LobbyData::battleID.name}"
        ) { battleID, battleIDListenerCanceler ->
            if (battleID == null || battleID == "") {
                return@listenPrimitiveValue
            }

            databaseHandler.getUserID { userID ->
                databaseHandler.setValue(
                    "${DataPaths.BATTLES}/$battleID/${BattleData::otherPlayerID.name}",
                    userID
                ) { battleIDListenerCanceler.invoke() }
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
        ) {
            if (it == null)
            // TODO: Error
                return@readReferenceValue Logger("ulrik").error("BattleData is null")
            databaseHandler.setValue(
                "${DataPaths.BATTLES}/$battleDataID/${BattleData::actions.name}",
                it.actions + listOf(action)
            ) {}
        }
    }

    private fun listenForActions(battleID: String) {
        databaseHandler.listenListValue<ActionData>(
            "${DataPaths.BATTLES}/$battleID/${BattleData::actions.name}",
            listenerCancelerConsumer = battleListenerCancelers::add
        ) { updatedActionData, _ ->
            if (updatedActionData == null) return@listenListValue
            actionListBuffer = if (actionListBuffer == null || lastReadActionIndex == -1) {
                updatedActionData
            } else {
                updatedActionData.subList(lastReadActionIndex + 1, updatedActionData.size)
            }
        }
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
        battleListenerCancelers.forEach { it.invoke() }
        resetActionDataBuffer()
        battleListenerCancelers = mutableListOf()
        battleID = null
    }

    fun getUsername(listener: Consumer<String?>) {
        databaseHandler.getUserID { userId ->
            databaseHandler.readPrimitiveValue("${DataPaths.USERNAME}/$userId", consumer = listener)
        }
    }

    fun getUsernameMap(listener: Consumer<Map<String, String>?>) {
        databaseHandler.readPrimitiveValue(
            "${DataPaths.USERNAME}",
            fail = { _, _ -> listener.accept(null) },
            consumer = listener
        )
    }

    fun setUsername(username: String, listener: Consumer<Boolean>) {
        databaseHandler.getUserID { userId ->
            databaseHandler.setValue(
                "${DataPaths.USERNAME}/$userId",
                username,
                fail = { _, _ -> listener.accept(false) }
            ) {
                listener.accept(true)
            }
        }
    }

    fun getLeaderboard(listener: Consumer<List<LeaderboardEntry>?>) {
        databaseHandler.readPrimitiveValue<Map<String, Long>>(
            "${DataPaths.LEADERBOARD}",
            fail = { _, _ -> listener.accept(null) }
        ) { board ->
            if (board == null) {
                listener.accept(listOf())
            } else {
                getUsernameMap { usernameMap ->
                    listener.accept(
                        board.entries
                            .sortedByDescending { entry -> entry.value }
                            .map { LeaderboardEntry(it.key, usernameMap?.get(it.key), it.value) }
                    )
                }
            }
        }
    }

    fun incrementScore(incrementBy: Long, listener: Consumer<Boolean>) {
        databaseHandler.getUserID { userId ->
            databaseHandler.readPrimitiveValue<Long>("${DataPaths.LEADERBOARD}/$userId") { before ->
                val new = (before ?: 0) + incrementBy
                databaseHandler.setValue("${DataPaths.LEADERBOARD}/$userId", new) {
                    listener.accept(true)
                }
            }
        }
    }
}

data class LeaderboardEntry(val userId: String, val username: String?, val score: Long)
