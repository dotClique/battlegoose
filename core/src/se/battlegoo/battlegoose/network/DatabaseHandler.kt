package se.battlegoo.battlegoose.network

import pl.mk5.gdx.fireapp.GdxFIRAuth
import pl.mk5.gdx.fireapp.GdxFIRDatabase
import pl.mk5.gdx.fireapp.auth.GdxFirebaseUser
import pl.mk5.gdx.fireapp.database.FilterType
import pl.mk5.gdx.fireapp.database.OrderByMode
import pl.mk5.gdx.fireapp.promises.ListenerPromise
import pl.mk5.gdx.fireapp.promises.Promise
import se.battlegoo.battlegoose.datamodels.ActionData
import se.battlegoo.battlegoose.datamodels.BattleData
import se.battlegoo.battlegoose.datamodels.LobbyData
import java.util.function.Consumer

typealias ConversionFunc<T> = (Map<String, Any>) -> T
typealias ListConversionFunc<T> = (List<Map<String, Any>>) -> List<T>

class DatabaseHandler {

    fun signInAnonymously(): Promise<GdxFirebaseUser> {
        return GdxFIRAuth.inst().signInAnonymously()
    }

    fun getUserID(consumer: Consumer<String>) {
        consumer.accept(GdxFIRAuth.inst().currentUser.userInfo.uid)
    }

    inline fun <reified T : Any> readFilteredValue(
        databasePath: String,
        order: OrderByMode,
        orderByArg: String,
        filter: FilterType,
        filterArg: String,
        consumer: Consumer<T?>,
    ) {
        GdxFIRDatabase.inst()
            .inReference(databasePath)
            .filter(filter, filterArg)
            .orderBy(order, orderByArg)
            .readValue(T::class.java)
            .then<T> { consumer.accept(it) }
    }

    inline fun <reified T : Any> listenPrimitiveValue(
        databasePath: String,
        consumer: Consumer<T?>
    ): ListenerPromise<T> {
        val listener = GdxFIRDatabase.inst().inReference(databasePath).onDataChange(T::class.java)
        listener.then<T> {
            consumer.accept(it)
        }
        return listener
    }

    inline fun <reified T : Any> listenListValue(
        databasePath: String,
        consumer: Consumer<List<T>?>
    ): ListenerPromise<List<Map<String, Any>>>? {
        @Suppress("UNCHECKED_CAST")
        return when (T::class) {
            ActionData::class -> listenListDataClass(
                databasePath,
                consumer,
                this::convertToActionDataList as ListConversionFunc<T>
            )
            else -> null
        }
    }

    inline fun <reified T : Any> readPrimitiveValue(databasePath: String, consumer: Consumer<T?>) {
        GdxFIRDatabase.inst().inReference(databasePath).readValue(T::class.java).then<T> {
            consumer.accept(it)
        }
    }

    inline fun <reified T : Any> readReferenceValue(databasePath: String, consumer: Consumer<T?>) {
        @Suppress("UNCHECKED_CAST")
        when (T::class) {
            LobbyData::class ->
                readDataClass(databasePath, consumer, this::convertToLobby as ConversionFunc<T>)
            BattleData::class ->
                readDataClass(
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
        return readPrimitiveValue<Map<String, Any>>(
            databasePath,
            Consumer { consumerData ->
                if (consumerData == null) {
                    consumer.accept(null)
                    return@Consumer
                }
                consumer.accept(conversionFunc(consumerData))
            }
        )
    }

    fun <T> listenListDataClass(
        databasePath: String,
        consumer: Consumer<List<T>?>,
        listConversionFunc: ListConversionFunc<T>
    ): ListenerPromise<List<Map<String, Any>>> {
        return listenPrimitiveValue(
            databasePath,
            Consumer { consumerData ->
                if (consumerData == null) {
                    consumer.accept(null)
                    return@Consumer
                }
                consumer.accept(listConversionFunc(consumerData))
            }
        )
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

    fun convertToLobby(data: Map<String, Any>): LobbyData {
        val lobbyID = data["lobbyID"].toString()
        val hostID = data[LobbyData::hostID.name] as String
        val otherPlayerID = data[LobbyData::otherPlayerID.name] as String
        val shouldStart = data[LobbyData::shouldStart.name] as Boolean
        return LobbyData(lobbyID, hostID, otherPlayerID, shouldStart)
    }

    fun convertToBattle(battleData: Map<String, Any>): BattleData {
        val battleID = battleData[BattleData::battleID.name] as String
        val hostID = battleData[BattleData::hostID.name] as String
        val otherPlayerID = battleData[BattleData::otherPlayerID.name] as String

        @Suppress("UNCHECKED_CAST")
        val actionsRaw = battleData[BattleData::actions.name] as List<Map<String, Any>>?
        val actions: List<ActionData> = actionsRaw
            ?.map(::convertToActionData)
            ?: emptyList()
        return BattleData(battleID, hostID, otherPlayerID, actions)
    }

    private fun convertToActionData(actionData: Map<String, Any>): ActionData {
        return ActionData(
            actionData[ActionData::action.name] as String,
            actionData[ActionData::playerID.name] as String
        )
    }

    fun convertToActionDataList(actionDataList: List<Map<String, Any>>): List<ActionData> {
        return actionDataList.map(::convertToActionData)
    }
}
