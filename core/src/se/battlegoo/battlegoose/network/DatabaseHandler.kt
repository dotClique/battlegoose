package se.battlegoo.battlegoose.network

import pl.mk5.gdx.fireapp.GdxFIRAuth
import pl.mk5.gdx.fireapp.GdxFIRDatabase
import pl.mk5.gdx.fireapp.auth.GdxFirebaseUser
import pl.mk5.gdx.fireapp.promises.Promise
import se.battlegoo.battlegoose.Lobby
import java.util.function.Consumer

class DatabaseHandler {
    fun signInAnonymously(): Promise<GdxFirebaseUser> {
        return GdxFIRAuth.inst().signInAnonymously()
    }

    fun getUserID(consumer: Consumer<String>) {
        this.signInAnonymously().then<GdxFirebaseUser> { user ->
            consumer.accept(user.userInfo.uid)
        }
    }

    inline fun <reified T : Any> readPrimitiveValue(databasePath: String, consumer: Consumer<T?>) {
        GdxFIRDatabase.inst()
            .inReference(databasePath)
            .readValue(T::class.java).then<T> {
                consumer.accept(it)
            }
    }

    inline fun <reified T : Any> readReferenceValue(databasePath: String, consumer: Consumer<T?>) {
        when (T::class) {
            Lobby::class -> readPrimitiveValue<HashMap<String, Any>>(
                databasePath,
                Consumer { data ->
                    if (data == null) {
                        consumer.accept(null)
                        return@Consumer
                    }
                    consumer.accept(convertToLobby(data) as T)
                }
            )
        }
    }

    fun setValue(databasePath: String, value: Any): Promise<Void> {
        return GdxFIRDatabase.inst().inReference(databasePath).setValue(value)
    }

    fun pushValue(databasePath: String, value: Any): Promise<Void> {
        return GdxFIRDatabase.inst().inReference(databasePath).push().setValue(value)
    }

    fun convertToLobby(data: HashMap<String, Any>): Lobby {
        val hostID = data["hostID"].toString()
        val otherPlayerID = data["otherPlayerID"].toString()
        val shouldStart = data["shouldStart"].toString().toBoolean()
        return Lobby(hostID, otherPlayerID, shouldStart)
    }
}
