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
        return lobbyData.otherPlayerID.isEmpty() || lobbyData.otherPlayerID == userID
    }

    private fun setOtherPlayerIDInLobby(
        lobbyID: String,
        userID: String,
        onFail: FailFunc = { _, throwable -> throw throwable },
        onSuccess: () -> Unit
    ) {
        databaseHandler.setValue(
            DbPath.Lobbies[lobbyID][LobbyData::otherPlayerID],
            userID,
            onFail,
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
                databaseHandler.listen(DbPath.Lobbies[lobbyID][LobbyData::otherPlayerID]) { otherPlayerID,
                                                                                            cancelListener ->
                    val lobbyData = LobbyData(lobbyID, userID, otherPlayerID ?: "")
                    if (!otherPlayerID.isNullOrEmpty())
                        listener(
                            lobbyData,
                            CreateLobbyStatus.OTHER_PLAYER_JOINED,
                            cancelListener
                        )
                    else listener(lobbyData, CreateLobbyStatus.OPEN, cancelListener)
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
        databaseHandler.read(DbPath.RandomOpponentData[RandomOpponentData::availableLobby]) { lobbyID ->
            consumer.accept(lobbyID)
        }

    // typealias RandomPairingListener = (RandomOpponentStatus, ListenerCanceler, ListenerCanceler) -> Unit,
    private fun purgeQueue(
        onFail: FailFunc = { _, throwable -> throw throwable },
        onSuccess: () -> Unit = {}
    ) {
        databaseHandler.setValue(
            DbPath.RandomOpponentData[RandomOpponentData::lastUpdated],
            Date().time, onFail
        ) {
            databaseHandler.setValue(DbPath.RandomOpponentQueue, listOf(), onFail) {
                onSuccess()
            }
        }
    }

    private fun purgeQueueIfTimeout(
        onFail: FailFunc = { _, throwable -> throw throwable },
        onUpdate: (Boolean) -> Unit
    ) =
        databaseHandler.read(
            DbPath.RandomOpponentData[RandomOpponentData::lastUpdated],
            onFail
        ) { lastUpdated ->
            val timeoutMs = 10000

            if (lastUpdated != null && Date().time - lastUpdated < timeoutMs) {
                onUpdate(false)
                return@read
            }
            purgeQueue(onFail) { onUpdate(true) }
        }

    private fun addPlayerToQueue(
        userID: String,
        queue: List<String>,
        listener: RandomPairingListener,
        cannotLeaveQueue: LeaveRandomPairingQueue
    ): List<String> {
        if (queue.contains(userID)) return queue

        val updatedQueue = queue + userID

        databaseHandler.setValue(DbPath.RandomOpponentQueue, updatedQueue) {
            listener(RandomPairingStatus.JOINED_QUEUE, cannotLeaveQueue)
        }
        return updatedQueue
    }

    private fun setAvailableLobby(availableLobby: String, onSuccess: () -> Unit) {
        databaseHandler.setValue(
            DbPath.RandomOpponentData,
            RandomOpponentData(availableLobby, Date().time),
            onSuccess = onSuccess
        )
    }

    private fun popQueue(queue: List<String>, onSuccess: () -> Unit) {
        databaseHandler.setValue(
            DbPath.RandomOpponentQueue,
            queue.subList(1, queue.size),
            onSuccess = onSuccess
        )
    }

    fun tryRequestOpponent(
        listener: RandomPairingListener,
        onFail: FailFunc = { _, throwable -> throw throwable },
    ) {
        var processingQueue = false
        databaseHandler.listen(
            DbPath.RandomOpponentQueue,
        ) { updatedQueueData, queueListenerCanceler ->
            val leaveQueue: LeaveRandomPairingQueue = { onFail, onSuccess ->
                processingQueue = true // To not add yourself to queue again
                purgeQueue({ str, throwable ->
                    processingQueue = false
                    onFail(str, throwable)
                }) {
                    queueListenerCanceler()
                    onSuccess()
                }
            }
            val cannotLeaveQueue: LeaveRandomPairingQueue = { _, _ -> }
            if (processingQueue) {
                return@listen
            }

            // Preemptively setting this to ensure no parallel lobby creation/joining
            processingQueue = true

            // Constantly checking if the queue can be purged
            purgeQueueIfTimeout(onFail) { didPurge ->
                // purgedQueue is null if the queue was not purged, keep the old queue.
                var queue = if (!didPurge) updatedQueueData ?: listOf() else listOf()

                databaseHandler.getUserID { userID ->
                    // Add this player to the queue if the player is not in the queue
                    queue = addPlayerToQueue(userID, queue, listener, cannotLeaveQueue)

                    // Here the queue up to date, thus we can check whether
                    // the player can do  anything
                    if (queue.first() != userID) {
                        processingQueue = false
                        listener(RandomPairingStatus.WAITING_IN_QUEUE, leaveQueue)
                        return@getUserID
                    }
                    listener(RandomPairingStatus.FIRST_IN_QUEUE, cannotLeaveQueue)
                    // The player is here first in queue, and either joins an available lobby
                    // for random matchmaking or creates his own lobby if there is
                    // another player in the queue.
                    checkRandomLobbyAvailability { availableLobbyID ->
                        if (!availableLobbyID.isNullOrEmpty()) {
                            // The lobby is available, clear the field to ensure noboby else enters
                            setAvailableLobby("") {
                                joinLobby(
                                    availableLobbyID,
                                    onFail = { reason, throwable ->
                                        queueListenerCanceler()
                                        onFail(reason, throwable)
                                    }
                                ) { joinLobbyStatus, cancelJoinLobbyListener ->
                                    when (joinLobbyStatus) {
                                        is JoinLobbyStatus.Ready -> {
                                            queueListenerCanceler()
                                            popQueue(queue) {
                                                listener(
                                                    RandomPairingStatus.JOINED_LOBBY,
                                                    cannotLeaveQueue
                                                )
                                            }
                                        }
                                        is JoinLobbyStatus.StartBattle -> {
                                            cancelJoinLobbyListener()
                                            listener(
                                                RandomPairingStatus.START_BATTLE,
                                                cannotLeaveQueue
                                            )
                                        }
                                        else -> {
                                            // Failed to join lobby
                                            processingQueue = false
                                            popQueue(queue) {
                                                listener(
                                                    RandomPairingStatus.FAILED,
                                                    cannotLeaveQueue
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (queue.size > 1) {
                            var createdLobby = false
                            createLobby { lobby, createLobbyStatus, cancelOtherPlayerIDListener ->
                                if (!createdLobby) {
                                    createdLobby = true
                                    setAvailableLobby(lobby.lobbyID) {
                                        queueListenerCanceler()
                                        popQueue(queue) {
                                            listener(
                                                RandomPairingStatus.CREATED_LOBBY,
                                                cannotLeaveQueue
                                            )
                                        }
                                    }
                                }
                                when (createLobbyStatus) {
                                    CreateLobbyStatus.OPEN -> {
                                        listener(
                                            RandomPairingStatus.WAITING_FOR_OTHER_PLAYER,
                                            cannotLeaveQueue
                                        )
                                    }
                                    CreateLobbyStatus.OTHER_PLAYER_JOINED -> {
                                        cancelOtherPlayerIDListener()
                                        startBattle(lobby.lobbyID, onFail) {
                                            listener(
                                                RandomPairingStatus.START_BATTLE,
                                                cannotLeaveQueue
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            processingQueue = false
                            listener(RandomPairingStatus.WAITING_FOR_OTHER_PLAYER, leaveQueue)
                        }
                    }
                }
            }
        }
    }

    fun joinLobby(
        lobbyID: String,
        onFail: FailFunc = { _, throwable -> throw throwable },
        battleListener: BiConsumer<JoinLobbyStatus, ListenerCanceler>,
    ) {
        databaseHandler.getUserID { userID ->
            databaseHandler.read(DbPath.Lobbies[lobbyID], onFail) { lobby ->
                if (lobby == null) {
                    battleListener.accept(JoinLobbyStatus.DoesNotExist) {}
                    return@read
                }

                if (!userCanJoinLobby(userID, lobby)) {
                    battleListener.accept(JoinLobbyStatus.Full) {}
                    return@read
                }
                setOtherPlayerIDInLobby(
                    lobbyID,
                    userID,
                    onFail = { _, _ ->
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
                    listenForBattleStart(lobbyID, onFail) { battleID, cancelLobbyListener ->
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
        onFail: FailFunc = { _, throwable -> throw throwable },
        onLeftLobby: () -> Unit = {}
    ) {
        databaseHandler.setValue(DbPath.Lobbies[lobbyID][LobbyData::otherPlayerID], "", onFail) {
            onLeftLobby()
        }
    }

    fun deleteLobby(
        lobbyID: String,
        onFail: FailFunc = { _, throwable -> throw throwable },
        onSuccess: () -> Unit = {}
    ) {
        databaseHandler.deleteValue(DbPath.Lobbies[lobbyID], onFail, onSuccess)
    }

    fun startBattle(
        lobbyID: String,
        onFail: FailFunc = { _, throwable -> throw throwable },
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
                ) // The otherPlayerId is set by that other player for confirmation.

            databaseHandler.setValue(DbPath.Battles[battleID], initialBattleData, onFail) {
                databaseHandler.setValue(
                    DbPath.Lobbies[lobbyID][LobbyData::battleID],
                    battleID,
                    onFail
                ) {
                    // Listen for the other player to join the battle
                    databaseHandler.listen(
                        DbPath.Battles[battleID][BattleData::otherPlayerID],
                        onFail
                    ) { otherPlayerID, otherPlayerIDListenerCanceler ->
                        if (otherPlayerID == null || otherPlayerID == "") {
                            return@listen
                        }
                        this.battleID = battleID
                        databaseHandler.deleteValue(DbPath.Lobbies[lobbyID]) {}
                        otherPlayerIDListenerCanceler()
                        listenForActions(battleID, onFail)
                        onCreated(initialBattleData)
                    }
                }
            }
        }
    }

    fun listenForBattleStart(
        lobbyID: String,
        onFail: FailFunc = { _, throwable -> throw throwable },
        consumer: BiConsumer<String?, ListenerCanceler>,
    ) {
        // Have to listen on the battleID to also run the function when otherPlayerID changes.
        databaseHandler.listen(DbPath.Lobbies[lobbyID], onFail = onFail) { lobbyData,
                                                                           cancelLobbyListener ->
            if (lobbyData?.battleID == null || lobbyData.battleID == "") {
                consumer.accept(lobbyData?.battleID, cancelLobbyListener)
                return@listen
            }
            databaseHandler.getUserID { userID ->
                databaseHandler.setValue(
                    DbPath.Battles[lobbyData.battleID][BattleData::otherPlayerID],
                    userID,
                    onFail
                ) {
                    // The lobby will now be deleted
                    cancelLobbyListener()
                    // Keep track of the battleID of the joined battle
                    this.battleID = lobbyData.battleID
                    // Passing empty canceler to not call it twice
                    consumer.accept(lobbyData.battleID) {}
                    listenForActions(lobbyData.battleID, onFail)
                }
            }
        }
    }

    /** Pushes an action to the actionlist. */
    fun postAction(
        action: ActionData,
        onFail: FailFunc = { _, throwable -> throw throwable },
        onSuccess: () -> Unit = {}
    ) {
        val battleDataID =
            battleID
                ?: return onFail(
                    "BattleID is not set in MultiplayerService.",
                    IllegalStateException()
                )
        databaseHandler.read(DbPath.Battles[battleDataID], onFail) {
            if (it == null)
                return@read onFail(
                    "There is no BattleData with the given BattleID.",
                    IllegalStateException()
                )
            databaseHandler.setValue(
                DbPath.Battles[battleDataID][BattleData::actions],
                it.actions + listOf(action)
            ) { onSuccess() }
        }
    }

    private fun listenForActions(
        battleID: String,
        onFail: FailFunc = { _, throwable -> throw throwable },
    ) {
        databaseHandler.listen(
            DbPath.Battles[battleID][BattleData::actions],
            onFail,
            listenerCancelerConsumer = battleListenerCancelers::add
        ) { updatedActionData, _ ->
            if (updatedActionData == null) {
                if (actionListBuffer != null)
                    onFail(
                        "Retrieved ActionData list is null although the local buffer is not null.",
                        IllegalStateException()
                    )
                // Otherwise, it is fine that the updatedActionData can be null
                return@listen
            }

            if (actionListBuffer == null || lastReadActionIndex == -1) {
                actionListBuffer = updatedActionData
            } else {
                actionListBuffer =
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
            onFail = { _, _ -> listener.accept(null) },
            consumer = listener
        )
    }

    fun setUsername(username: String, listener: Consumer<Boolean>) {
        databaseHandler.getUserID { userId ->
            databaseHandler.setValue(
                DbPath.Username[userId],
                username,
                onFail = { _, _ -> listener.accept(false) }
            ) { listener.accept(true) }
        }
    }

    fun getLeaderboard(listener: Consumer<List<LeaderboardEntry>?>) {
        databaseHandler.read(DbPath.Leaderboard, onFail = { _, _ -> listener.accept(null) }) { board
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

typealias LeaveRandomPairingQueue = (FailFunc, () -> Unit) -> Unit
typealias RandomPairingListener = (RandomPairingStatus, LeaveRandomPairingQueue) -> Unit

data class LeaderboardEntry(val userId: String, val username: String?, val score: Long)
