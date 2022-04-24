package se.battlegoo.battlegoose.network

import com.badlogic.gdx.utils.Logger
import java.util.Date
import java.util.function.BiConsumer
import java.util.function.Consumer
import se.battlegoo.battlegoose.datamodels.ActionData
import se.battlegoo.battlegoose.datamodels.BattleData
import se.battlegoo.battlegoose.datamodels.LobbyData
import se.battlegoo.battlegoose.datamodels.RandomOpponentData

object MultiplayerService {
    private val databaseHandler = DatabaseHandler()
    private var battleID: String? = null

    private var lastReadActionIndex: Int = -1
    private var actionListBuffer: List<ActionData>? = null

    private var battleListenerCancelers = mutableListOf<ListenerCanceler>()

    fun getUserID(consumer: Consumer<String>) = databaseHandler.getUserID(consumer)

    private fun userCanJoinLobby(userID: String, lobbyData: LobbyData): Boolean {
        return lobbyData.otherPlayerID.isNotEmpty() && lobbyData.otherPlayerID != userID
    }

    private fun setOtherPlayerIDInLobby(
            lobbyID: String,
            userID: String,
            fail: (String, Throwable) -> Unit = { _, throwable -> throw throwable },
            onSuccess: () -> Unit
    ) {
        databaseHandler.setValue(
                DbPath.Lobbies[lobbyID][LobbyData::otherPlayerID],
                userID,
                fail,
                onSuccess
        )
    }

    private fun generateReadableUID(): String {
        return (1..6).map { ('A'..'Z').random() }.joinToString("")
    }

    fun createLobby(listener: (LobbyData, CreateLobbyStatus, ListenerCanceler) -> Unit) {
        databaseHandler.getUserID { userID ->
            val lobbyID = generateReadableUID()
            databaseHandler.setValue(DbPath.Lobbies[lobbyID], LobbyData(lobbyID, userID)) {
                // Listen for other player to join lobby
                listener(LobbyData(lobbyID, userID), CreateLobbyStatus.OPEN) {}
                databaseHandler.listen(DbPath.Lobbies[lobbyID][LobbyData::otherPlayerID]) {
                        otherPlayerID,
                        cancelListener ->
                    val lobbyData = LobbyData(lobbyID, userID, otherPlayerID ?: "")
                    if (!otherPlayerID.isNullOrEmpty()) {
                        listener(lobbyData, CreateLobbyStatus.OTHER_PLAYER_JOINED, cancelListener)
                        return@listen
                    }
                    listener(lobbyData, CreateLobbyStatus.OPEN, cancelListener)
                }
            }
        }
    }

    // TODO: Remove this function
    private fun tryCreateLobby(listener: Consumer<LobbyData>) {
        databaseHandler.getUserID { userID ->
            val lobbyID = generateReadableUID()
            databaseHandler.setValue(DbPath.Lobbies[lobbyID], LobbyData(lobbyID, userID)) {
                listener.accept(LobbyData(lobbyID, userID))
            }
        }
    }

    private fun checkRandomLobbyAvailability(consumer: Consumer<String?>) =
            databaseHandler.read(DbPath.RandomOpponentData[RandomOpponentData::availableLobby]) {
                    lobbyID ->
                consumer.accept(lobbyID)
            }

    private fun purgeQueue(
            listener: Consumer<RandomOpponentStatus>,
            purgedQueueConsumer: Consumer<List<String>?>
    ) {
        databaseHandler.setValue(
                DbPath.RandomOpponentData[RandomOpponentData::lastUpdated],
                Date().time
        ) {
            databaseHandler.setValue(DbPath.RandomOpponentQueue, listOf()) {
                listener.accept(RandomOpponentStatus.TIMEOUT_INACTIVE_PLAYER)
                purgedQueueConsumer.accept(listOf())
            }
        }
    }

    private fun purgeQueueIfTimeout(
            listener: Consumer<RandomOpponentStatus>,
            purgedQueueConsumer: Consumer<List<String>?>
    ) =
            databaseHandler.read(DbPath.RandomOpponentData[RandomOpponentData::lastUpdated]) {
                    lastUpdated ->
                val timeoutMs = 10000

                if (lastUpdated != null && Date().time - lastUpdated < timeoutMs) {
                    purgedQueueConsumer.accept(null)
                    return@read
                }
                purgeQueue(listener, purgedQueueConsumer)
            }

    private fun addPlayerToQueue(
            userID: String,
            queue: List<String>,
            listener: Consumer<RandomOpponentStatus>
    ): List<String> {
        if (queue.contains(userID)) return queue

        val updatedQueue = queue + userID

        databaseHandler.setValue(DbPath.RandomOpponentQueue, updatedQueue) {
            listener.accept(RandomOpponentStatus.JOIN_QUEUE)
        }
        return updatedQueue
    }

    private fun listenForOtherRandomPlayerJoinLobby(
            lobbyID: String,
            listener: Consumer<RandomOpponentStatus>
    ) {
        databaseHandler.listen(DbPath.Lobbies[lobbyID][LobbyData::otherPlayerID]) {
                otherPlayerID,
                otherPlayerIDListenerCanceler ->
            if (!otherPlayerID.isNullOrEmpty()) {
                otherPlayerIDListenerCanceler.invoke()
                listener.accept(RandomOpponentStatus.OTHER_PLAYER_JOINED)
                return@listen
            }
            listener.accept(RandomOpponentStatus.WAITING_FOR_OTHER_PLAYER)
        }
    }

    private fun setAvailableLobby(availableLobby: String, callback: () -> Unit) {
        databaseHandler.setValue(
                DbPath.RandomOpponentData,
                RandomOpponentData(availableLobby, Date().time),
                onSuccess = callback
        )
    }

    private fun popQueue(queue: List<String>, callback: () -> Unit) {
        databaseHandler.setValue(
                DbPath.RandomOpponentQueue,
                queue.subList(0, queue.size - 1),
                onSuccess = callback
        )
    }

    fun tryCancelRequestOpponent(
            successListener: Consumer<Boolean>,
            queueCanceler: ListenerCanceler?
    ) {
        if (queueCanceler == null) return successListener.accept(false)
        queueCanceler.invoke()
        purgeQueue({}, {})
        successListener.accept(true)
    }

    fun tryRequestOpponent(
            listener: Consumer<RandomOpponentStatus>,
            lobbyIDConsumer: Consumer<String>,
            queueCanceler: Consumer<ListenerCanceler?>,
            cancelBattleJoinListener: Consumer<ListenerCanceler>
    ) {
        var processing = false
        databaseHandler.listen(
                DbPath.RandomOpponentQueue,
        ) { updatedQueueData, queueListenerCanceler ->
            queueCanceler.accept(queueListenerCanceler)
            if (processing) {
                return@listen
            }
            processing = true

            purgeQueueIfTimeout(listener) { purgedQueue ->
                var queue = purgedQueue ?: updatedQueueData ?: listOf()

                databaseHandler.getUserID { userID ->
                    queue = addPlayerToQueue(userID, queue, listener)

                    if (queue.last() != userID) {
                        processing = false
                        listener.accept(RandomOpponentStatus.WAITING_IN_QUEUE)
                        return@getUserID
                    }

                    this.checkRandomLobbyAvailability { availableLobbyID ->
                        if (!availableLobbyID.isNullOrEmpty()) {
                            queueCanceler.accept(null)
                            setAvailableLobby("") {
                                joinLobby(
                                        availableLobbyID,
                                ) { _, _ -> // TODO: Needs to be updated to the new joinLobby api
                                    queueListenerCanceler.invoke()
                                    popQueue(queue) {
                                        listener.accept(RandomOpponentStatus.JOINED_LOBBY)
                                    }
                                }
                            }
                            return@checkRandomLobbyAvailability
                        }

                        if (queue.size > 1) {
                            queueCanceler.accept(null)
                            tryCreateLobby { lobby ->
                                setAvailableLobby(lobby.lobbyID) {
                                    listenForOtherRandomPlayerJoinLobby(lobby.lobbyID, listener)
                                    queueListenerCanceler.invoke()
                                    popQueue(queue) {
                                        lobbyIDConsumer.accept(lobby.lobbyID)
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

    fun joinLobby(
            lobbyID: String,
            fail: (String, Throwable) -> Unit = { _, throwable -> throw throwable },
            battleListener: BiConsumer<JoinLobbyStatus, ListenerCanceler>,
    ) {
        databaseHandler.getUserID { userID ->
            databaseHandler.read(DbPath.Lobbies[lobbyID], fail) { lobby ->
                if (lobby == null) {
                    battleListener.accept(JoinLobbyStatus.DoesNotExist) {}
                    return@read
                }

                if (userCanJoinLobby(userID, lobby)) {
                    battleListener.accept(JoinLobbyStatus.Full) {}
                    return@read
                }
                setOtherPlayerIDInLobby(
                        lobbyID,
                        userID,
                        fail = { _, _ ->
                            battleListener.accept(JoinLobbyStatus.NotAccessible) {}
                            Logger("battlegoose")
                                    .error("Failed in setOtherPlayerIDInLobby called in joinLobby")
                        }
                ) {
                    battleListener.accept(
                            JoinLobbyStatus.Ready(
                                    LobbyData(
                                            lobbyID,
                                            lobby.hostID,
                                            userID,
                                    )
                            )
                    ) {}
                    listenForBattleStart(lobbyID, fail) { battleID, cancelLobbyListener ->
                        when (battleID) {
                            null ->
                                    battleListener.accept(
                                            JoinLobbyStatus.NotAccessible,
                                            cancelLobbyListener
                                    )
                            "" ->
                                    battleListener.accept(
                                            JoinLobbyStatus.Ready(
                                                    LobbyData(
                                                            lobbyID,
                                                            lobby.hostID,
                                                            userID,
                                                            battleID
                                                    )
                                            ),
                                            cancelLobbyListener
                                    )
                            else ->
                                    battleListener.accept(
                                            JoinLobbyStatus.StartBattle(
                                                    LobbyData(
                                                            lobbyID,
                                                            lobby.hostID,
                                                            userID,
                                                            battleID
                                                    )
                                            ),
                                            cancelLobbyListener
                                    )
                        }
                    }
                }
            }
        }
    }

    fun leaveLobbyAsOtherPlayer(
            lobbyID: String,
            fail: (String, Throwable) -> Unit = { _, throwable -> throw throwable },
            onLeftLobby: () -> Unit = {}
    ) {
        databaseHandler.setValue(DbPath.Lobbies[lobbyID][LobbyData::otherPlayerID], "", fail) {
            onLeftLobby()
        }
    }

    fun deleteLobby(
            lobbyID: String,
            fail: (String, Throwable) -> Unit = { _, throwable -> throw throwable },
            onSuccess: () -> Unit = {}
    ) {
        databaseHandler.deleteValue(DbPath.Lobbies[lobbyID], fail, onSuccess)
    }

    fun startBattle(
            lobbyID: String,
            fail: (String, Throwable) -> Unit = { _, throwable -> throw throwable },
            onCreated: (initialBattleData: BattleData) -> Unit = {}
    ) {
        val battleID = generateReadableUID()
        databaseHandler.getUserID { userID ->
            val initialBattleData =
                    BattleData(
                            battleID,
                            userID,
                            "",
                            listOf()
                    ) // The otherPlayerId is set by that other player.

            databaseHandler.setValue(DbPath.Battles[battleID], initialBattleData, fail) {
                databaseHandler.setValue(
                        DbPath.Lobbies[lobbyID][LobbyData::battleID],
                        battleID,
                        fail
                ) {
                    // Listen for the other player to join the battle
                    databaseHandler.listen(
                            DbPath.Battles[battleID][BattleData::otherPlayerID],
                            fail
                    ) { otherPlayerID, otherPlayerIDListenerCanceler ->
                        if (otherPlayerID == null || otherPlayerID == "") {
                            return@listen
                        }
                        this.battleID = battleID
                        databaseHandler.deleteValue(DbPath.Lobbies[lobbyID]) {}
                        otherPlayerIDListenerCanceler()
                        listenForActions(battleID, fail)
                        onCreated(initialBattleData)
                    }
                }
            }
        }
    }

    fun listenForBattleStart(
            lobbyID: String,
            fail: (String, Throwable) -> Unit = { _, throwable -> throw throwable },
            consumer: BiConsumer<String?, ListenerCanceler>,
    ) {
        // Have to listen on the battleID to also run the function when otherPlayerID changes.
        databaseHandler.listen(
                DbPath.Lobbies[lobbyID],
                fail = fail // ERROR here
        ) { lobbyData, cancelLobbyListener ->
            if (lobbyData?.battleID == null || lobbyData.battleID == "") {
                consumer.accept(lobbyData?.battleID, cancelLobbyListener)
                return@listen
            }
            databaseHandler.getUserID { userID ->
                databaseHandler.setValue(
                        DbPath.Battles[lobbyData.battleID][BattleData::otherPlayerID],
                        userID,
                        fail
                ) {
                    consumer.accept(lobbyData.battleID) {}
                    // Passing empty canceler to not call it twice
                    this.battleID = lobbyData.battleID
                    cancelLobbyListener()
                    listenForActions(lobbyData.battleID, fail)
                }
            }
        }
    }

    /** Pushes an action to the actionlist. */
    fun postAction(
            action: ActionData,
            fail: (String, Throwable) -> Unit = { _, throwable -> throw throwable },
            onSuccess: () -> Unit = {}
    ) {
        val battleDataID =
                battleID
                        ?: return fail(
                                "",
                                IllegalStateException("BattleID is not set in MultiplayerService.")
                        )
        databaseHandler.read(DbPath.Battles[battleDataID], fail) {
            if (it == null)
                    return@read fail(
                            "",
                            IllegalStateException("There is no BattleData with the given BattleID.")
                    )
            databaseHandler.setValue(
                    DbPath.Battles[battleDataID][BattleData::actions],
                    it.actions + listOf(action)
            ) { onSuccess() }
        }
    }

    private fun listenForActions(
            battleID: String,
            fail: (String, Throwable) -> Unit = { _, throwable -> throw throwable },
    ) {
        databaseHandler.listen(
                DbPath.Battles[battleID][BattleData::actions],
                fail,
                listenerCancelerConsumer = battleListenerCancelers::add
        ) { updatedActionData, _ ->
            if (updatedActionData == null) {
                if (actionListBuffer != null)
                        fail(
                                "",
                                IllegalStateException(
                                        "Retrieved ActionData list is null although the local buffer is not null."
                                )
                        )
                // Otherwise, it is fine that the updatedActionData can be null
                return@listen
            }
            actionListBuffer =
                    if (actionListBuffer == null || lastReadActionIndex == -1) {
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
            databaseHandler.read(DbPath.Username[userId], consumer = listener)
        }
    }

    fun getUsernameMap(listener: Consumer<Map<String, String>?>) {
        databaseHandler.read(
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
            ) { listener.accept(true) }
        }
    }

    fun getLeaderboard(listener: Consumer<List<LeaderboardEntry>?>) {
        databaseHandler.read(DbPath.Leaderboard, fail = { _, _ -> listener.accept(null) }) { board
            ->
            if (board == null) {
                listener.accept(listOf())
            } else {
                getUsernameMap { usernameMap ->
                    listener.accept(
                            board.entries.sortedByDescending { entry -> entry.value }.map {
                                LeaderboardEntry(it.key, usernameMap?.get(it.key), it.value)
                            }
                    )
                }
            }
        }
    }

    fun incrementScore(incrementBy: Long, listener: Consumer<Boolean>) {
        databaseHandler.getUserID { userId ->
            databaseHandler.read(DbPath.Leaderboard[userId]) { before ->
                val new = (before ?: 0) + incrementBy
                databaseHandler.setValue(DbPath.Leaderboard[userId], new) { listener.accept(true) }
            }
        }
    }
}

data class LeaderboardEntry(val userId: String, val username: String?, val score: Long)
