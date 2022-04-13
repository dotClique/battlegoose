package se.battlegoo.battlegoose.network

import pl.mk5.gdx.fireapp.database.FilterType
import pl.mk5.gdx.fireapp.database.OrderByMode
import pl.mk5.gdx.fireapp.promises.Promise
import se.battlegoo.battlegoose.datamodels.ActionData
import se.battlegoo.battlegoose.datamodels.BattleData
import se.battlegoo.battlegoose.datamodels.LobbyData
import java.util.*
import java.util.function.Consumer

object MultiplayerService {
    private val databaseHandler = DatabaseHandler()
    lateinit var userID: String
        private set

    init {
        databaseHandler.signInAnonymously()
        databaseHandler.getUserID { id -> userID = id }
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
            databaseHandler.readPrimitiveValue<Any>(DataPaths.RANDOM_OPPONENT_QUEUE.toString()) { data ->
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

    fun startBattle(lobbyID: String) {
        val lobbyConsumer =
            Consumer<LobbyData?> { lobby ->
                if (lobby == null) return@Consumer // TODO: Error handling here
                val initialBattleData =
                    BattleData(
                        lobby.hostID,
                        "",
                        listOf(ActionData("Initial Action"))
                    ) // The otherPlayerId is set by that other player.
                databaseHandler.pushValue(DataPaths.BATTLES.toString(), initialBattleData)

            }
        databaseHandler.readReferenceValue("${DataPaths.LOBBIES}/$lobbyID", lobbyConsumer)
    }

    fun joinBattle(lobbyID: String) {
        val battleConsumer = Consumer<HashMap<String, Any>?> battle@{
            val battleDataID =
                it?.keys?.toList()?.get(0) ?: return@battle // TODO: Error handling here
            databaseHandler.setValue(
                "${DataPaths.BATTLES}/$battleDataID/otherPlayerID",
                userID
            )
        }
        val lobbyConsumer =
            Consumer<LobbyData?> { lobby ->
                if (lobby == null) return@Consumer // TODO: Error handling here
                databaseHandler.readFilteredValue(
                    DataPaths.BATTLES.toString(),
                    OrderByMode.ORDER_BY_CHILD,
                    "hostID",
                    FilterType.EQUAL_TO,
                    lobby.hostID,
                    battleConsumer
                )
            }
        databaseHandler.readReferenceValue(
            "${DataPaths.LOBBIES}/$lobbyID", lobbyConsumer
        )
        databaseHandler.deleteValue("${DataPaths.LOBBIES}/$lobbyID")

    }

}
