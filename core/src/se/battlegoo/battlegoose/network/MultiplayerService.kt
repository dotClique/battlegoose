package se.battlegoo.battlegoose.network

import pl.mk5.gdx.fireapp.promises.Promise
import se.battlegoo.battlegoose.Lobby
import java.util.LinkedList
import java.util.function.Consumer
import kotlin.collections.ArrayList

object MultiplayerService {
    private val databaseHandler = DatabaseHandler()

    init {
        databaseHandler.signInAnonymously()
    }

    private fun userCanJoinLobby(userID: String, lobby: Lobby): Boolean {
        return lobby.otherPlayerID.isNotEmpty() && lobby.otherPlayerID != userID
    }

    private fun joinLobby(lobbyID: String, userID: String): Promise<Void> {
        return databaseHandler.setValue("${DataPaths.LOBBIES}/$lobbyID/otherPlayerID", userID)
    }

    fun tryCreateLobby(listener: Consumer<Lobby>) {
        databaseHandler.getUserID { userID ->
            databaseHandler.pushValue(DataPaths.LOBBIES.toString(), Lobby(userID)).then<Void> {
                listener.accept(Lobby(userID))
            }
        }
    }

    fun tryRequestOpponent(listener: Consumer<Boolean>) {
        databaseHandler.getUserID { userID ->
            databaseHandler.readPrimitiveValue<Any>(
                DataPaths.RANDOM_OPPONENT_QUEUE.toString()
            ) { data ->
                val queue: LinkedList<String> = if (data !is ArrayList<*>) {
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

    fun tryJoinLobby(lobbyID: String, listener: Consumer<Pair<LobbyStatus, Lobby?>>) {
        databaseHandler.readReferenceValue<Lobby>(
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
                                LobbyStatus.READY, Lobby(lobby.hostID, userID, lobby.shouldStart)
                            )
                        )
                    }
                }
            }
        )
    }
}
