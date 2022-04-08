package se.battlegoo.battlegoose.network

import se.battlegoo.battlegoose.datamodels.BattleData
import com.badlogic.gdx.utils.Logger
import java.util.LinkedList
import java.util.function.Consumer
import kotlin.collections.ArrayList
import pl.mk5.gdx.fireapp.promises.Promise
import se.battlegoo.battlegoose.datamodels.LobbyData
import se.battlegoo.battlegoose.datamodels.ActionData

object MultiplayerService {
    private val databaseHandler = DatabaseHandler()

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
            databaseHandler.readPrimitiveValue<Any>(DataPaths.RANDOM_OPPONENT_QUEUE.toString()) {
                    data ->
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

                    databaseHandler.getUserID { userID ->
                        if (userCanJoinLobby(userID, lobby)) {
                            listener.accept(Pair(LobbyStatus.FULL, null))
                            return@getUserID
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
                }
        )
    }

    fun startBattle(lobbyID: String) {
        val logger = Logger("ulrik")
        logger.error("Hello")
        val lobbyConsumer =
                Consumer<LobbyData?> { lobby ->
                    if (lobby == null) return@Consumer logger.error("Is null") // TODO: Error handling here
                    val initialBattleData =
                            BattleData(
                                    lobby.hostID,
                                    "",
                                listOf(ActionData("initialAction"))
                            ) // The otherPlayerId is set by that other player.
                    databaseHandler.pushValue(DataPaths.BATTLES.toString(), initialBattleData)

                }

        databaseHandler.readReferenceValue("${DataPaths.LOBBIES}/$lobbyID", lobbyConsumer)
    }
}
