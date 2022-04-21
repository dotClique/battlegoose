package se.battlegoo.battlegoose.network

import com.badlogic.gdx.utils.Logger
import se.battlegoo.battlegoose.datamodels.ActionData
import se.battlegoo.battlegoose.datamodels.BattleData
import se.battlegoo.battlegoose.datamodels.LobbyData
import se.battlegoo.battlegoose.datamodels.RandomOpponentData
import java.util.Date
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
            DbPath.Lobbies[lobbyID][LobbyData::otherPlayerID],
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
                DbPath.Lobbies[lobbyID], LobbyData(lobbyID, userID)
            ) {
                listener.accept(LobbyData(lobbyID, userID))
            }
        }
    }

    private fun checkRandomLobbyAvailability(consumer: Consumer<String?>) =
        databaseHandler.readPrimitive(
            DbPath.RandomOpponentData[RandomOpponentData::availableLobby]
        ) { lobbyID -> consumer.accept(lobbyID) }

    private fun purgeInactiveFromQueue(
        listener: Consumer<RandomOpponentStatus>,
        purgedQueueConsumer: Consumer<List<String>?>
    ) = databaseHandler.readPrimitive(
        DbPath.RandomOpponentData[RandomOpponentData::lastUpdated]
    ) { lastUpdated ->
        val now = Date().time
        val timeoutMs = 10000

        if (lastUpdated != null && now - lastUpdated < timeoutMs) {
            purgedQueueConsumer.accept(null)
            return@readPrimitive
        }

        databaseHandler.setValue(DbPath.RandomOpponentData[RandomOpponentData::lastUpdated], now) {
            databaseHandler.setValue(
                DbPath.RandomOpponentQueue, listOf()
            ) {
                listener.accept(RandomOpponentStatus.TIMEOUT_INACTIVE_PLAYER)
                purgedQueueConsumer.accept(listOf())
            }
        }
    }

    private fun addPlayerToQueue(
        userID: String,
        queue: List<String>,
        listener: Consumer<RandomOpponentStatus>
    ) {
        if (queue.contains(userID)) return

        databaseHandler.setValue(DbPath.RandomOpponentQueue, queue + userID) {
            listener.accept(RandomOpponentStatus.JOIN_QUEUE)
        }
    }

    private fun listenForOtherPlayerJoinLobby(
        lobbyID: String,
        listener: Consumer<RandomOpponentStatus>
    ) {
        databaseHandler.listenPrimitive(
            DbPath.Lobbies[lobbyID][LobbyData::otherPlayerID]
        ) { otherPlayerID, otherPlayerIDListenerCanceler ->
            if (!otherPlayerID.isNullOrEmpty()) {
                otherPlayerIDListenerCanceler.invoke()
                listener.accept(RandomOpponentStatus.OTHER_PLAYER_JOINED)
                return@listenPrimitive
            }
            listener.accept(RandomOpponentStatus.WAITING_FOR_OTHER_PLAYER)
        }
    }

    private fun setAvailableLobby(availableLobby: String, callback: () -> Unit) {
        databaseHandler.setValue(
            DbPath.RandomOpponentData,
            RandomOpponentData(availableLobby, Date().time),
            callback = callback
        )
    }

    private fun popQueue(queue: List<String>, callback: () -> Unit) {
        databaseHandler.setValue(
            DbPath.RandomOpponentQueue,
            queue.subList(0, queue.size - 1),
            callback = callback
        )
    }

    fun tryRequestOpponent(listener: Consumer<RandomOpponentStatus>) {
        var processing = false
        databaseHandler.listenPrimitive(
            DbPath.RandomOpponentQueue,
        ) { updatedQueueData, queueListenerCanceler ->
            if (processing) {
                return@listenPrimitive
            }
            processing = true

            purgeInactiveFromQueue(listener) { purgedQueue ->
                val queue = purgedQueue ?: updatedQueueData ?: listOf()

                databaseHandler.getUserID { userID ->
                    addPlayerToQueue(userID, queue, listener)

                    if (queue.last() != userID) {
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
            databaseHandler.readDataModel(
                DbPath.Lobbies[lobbyID],
                consumer = { lobby ->
                    if (lobby == null) {
                        listener.accept(LobbyStatus.DoesNotExist)
                        return@readDataModel
                    }

                    if (userCanJoinLobby(userID, lobby)) {
                        listener.accept(LobbyStatus.Full)
                        return@readDataModel
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
                DbPath.Battles[battleID], initialBattleData
            ) {
                databaseHandler.setValue(
                    DbPath.Lobbies[lobbyID][LobbyData::battleID],
                    battleID
                ) {
                    // Listen for the other player to join the battle
                    databaseHandler.listenPrimitive(
                        DbPath.Battles[battleID][BattleData::otherPlayerID]
                    ) { otherPlayerID, otherPlayerIDListenerCanceler ->
                        if (otherPlayerID == null || otherPlayerID.length < 0) {
                            return@listenPrimitive
                        }
                        this.battleID = battleID
                        databaseHandler.deleteValue(DbPath.Lobbies[lobbyID]) {}
                        otherPlayerIDListenerCanceler.invoke()
                        listenForActions(battleID)
                    }
                }
            }
        }
    }

    private fun joinBattle(lobbyID: String) {
        databaseHandler.listenPrimitive(
            DbPath.Lobbies[lobbyID][LobbyData::battleID]
        ) { battleID, battleIDListenerCanceler ->
            if (battleID == null || battleID == "") {
                return@listenPrimitive
            }

            databaseHandler.getUserID { userID ->
                databaseHandler.setValue(
                    DbPath.Battles[battleID][BattleData::otherPlayerID],
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
        databaseHandler.readDataModel(
            DbPath.Battles[battleDataID]
        ) {
            if (it == null)
            // TODO: Error
                return@readDataModel Logger("ulrik").error("BattleData is null")
            databaseHandler.setValue(
                DbPath.Battles[battleDataID][BattleData::actions],
                it.actions + listOf(action)
            ) {}
        }
    }

    private fun listenForActions(battleID: String) {
        databaseHandler.listenDataModel(
            DbPath.Battles[battleID][BattleData::actions],
            listenerCancelerConsumer = battleListenerCancelers::add
        ) { updatedActionData, _ ->
            if (updatedActionData == null) {
                // TODO: Error
                return@listenDataModel Logger("ulrik").error("updatedActionData is null")
            }
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
            databaseHandler.readPrimitive(DbPath.Username[userId], consumer = listener)
        }
    }

    fun getUsernameMap(listener: Consumer<Map<String, String>?>) {
        databaseHandler.readPrimitive(
            DbPath.Username,
            fail = { _, _ -> listener.accept(null) },
            consumer = listener
        )
    }

    fun setUsername(username: String, listener: Consumer<Boolean>) {
        databaseHandler.getUserID { userId ->
            databaseHandler.setValue(
                DbPath.Username[userId],
                username,
                fail = { _, _ -> listener.accept(false) }
            ) {
                listener.accept(true)
            }
        }
    }

    fun getLeaderboard(listener: Consumer<List<LeaderboardEntry>?>) {
        databaseHandler.readPrimitive(
            DbPath.Leaderboard,
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
            databaseHandler.readPrimitive(DbPath.Leaderboard[userId]) { before ->
                val new = (before ?: 0) + incrementBy
                databaseHandler.setValue(DbPath.Leaderboard[userId], new) {
                    listener.accept(true)
                }
            }
        }
    }
}

data class LeaderboardEntry(val userId: String, val username: String?, val score: Long)
