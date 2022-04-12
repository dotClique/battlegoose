package se.battlegoo.battlegoose.network

import com.badlogic.gdx.utils.Logger
import pl.mk5.gdx.fireapp.GdxFIRAuth
import pl.mk5.gdx.fireapp.GdxFIRDatabase
import pl.mk5.gdx.fireapp.auth.GdxFirebaseUser
import pl.mk5.gdx.fireapp.database.FilterType
import pl.mk5.gdx.fireapp.database.OrderByMode
import pl.mk5.gdx.fireapp.promises.Promise
import se.battlegoo.battlegoose.datamodels.ActionData
import se.battlegoo.battlegoose.datamodels.BattleData
import se.battlegoo.battlegoose.datamodels.LobbyData
import java.util.function.Consumer

typealias ConversionFunc<T> = (HashMap<String, Any>) -> T

class DatabaseHandler {
    fun signInAnonymously(): Promise<GdxFirebaseUser> {
        return GdxFIRAuth.inst().signInAnonymously()
    }

    fun getUserID(consumer: Consumer<String>) {
        this.signInAnonymously().then<GdxFirebaseUser> { user ->
            consumer.accept(user.userInfo.uid)
        }
    }

    inline fun <reified T : Any> readFilteredValue(
        databasePath: String,
        order: OrderByMode,
        orderByArg: String,
        filter: FilterType,
        filterArg: String,
        consumer: Consumer<T?>,
    ) {
        GdxFIRDatabase.inst().inReference(databasePath)
            .filter(filter, filterArg).orderBy(order, orderByArg)
            .readValue(T::class.java).then<T> {
                consumer.accept(it)
            }
    }

    inline fun <reified T : Any> readPrimitiveValue(databasePath: String, consumer: Consumer<T?>) {
        GdxFIRDatabase.inst().inReference(databasePath).readValue(T::class.java).then<T> {
            consumer.accept(it)
        }
    }

    inline fun <reified T : Any> readReferenceValue(databasePath: String, consumer: Consumer<T?>) {
        when (T::class) {
            LobbyData::class -> readDataClass(
                databasePath,
                consumer,
                this::convertToLobby as ConversionFunc<T>
            )
            BattleData::class -> readDataClass(
                databasePath,
                consumer,
                this::convertToBattle as ConversionFunc<T>
            )
        }
    }


    fun <T> readDataClass(
        databasePath: String,
        consumer: Consumer<T?>,
        conversionFunc: ConversionFunc<T>
    ) {
        return readPrimitiveValue<HashMap<String, Any>>(
            databasePath,
            Consumer { consumerData ->
                if (consumerData == null) {
                    consumer.accept(null);
                    return@Consumer
                }
                consumer.accept(conversionFunc(consumerData))
            })

    }

    fun setValue(databasePath: String, value: Any): Promise<Void> {
        return GdxFIRDatabase.inst().inReference(databasePath).setValue(value)
    }

    fun pushValue(databasePath: String, value: Any): Promise<Void> {
        return GdxFIRDatabase.inst().inReference(databasePath).push().setValue(value)
    }

    fun deleteValue(databasePath: String): Promise<Void> {
        return GdxFIRDatabase.inst().inReference(databasePath).removeValue()
    }


    fun convertToLobby(data: HashMap<String, Any>): LobbyData {
        val hostID = data["hostID"].toString()
        val otherPlayerID = data["otherPlayerID"].toString()
        val shouldStart = data["shouldStart"].toString().toBoolean()
        return LobbyData(hostID, otherPlayerID, shouldStart)
    }

    fun convertToBattle(battleData: HashMap<String, Any>): BattleData {
        val hostID = battleData["hostID"].toString()
        val otherPlayerID = battleData["otherPlayerID"].toString()
        val actionsSome = battleData["actions"]

        val logger = Logger("ulrik")
        logger.error("ActionsSome: $actionsSome, type: ${actionsSome!!::class}")

        val actions = listOf<ActionData>()
        return BattleData(hostID, otherPlayerID, actions)
    }
}
