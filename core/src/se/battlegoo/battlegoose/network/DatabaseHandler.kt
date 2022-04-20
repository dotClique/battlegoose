package se.battlegoo.battlegoose.network

import pl.mk5.gdx.fireapp.GdxFIRAuth
import pl.mk5.gdx.fireapp.GdxFIRDatabase
import pl.mk5.gdx.fireapp.auth.GdxFirebaseUser
import pl.mk5.gdx.fireapp.database.FilterType
import pl.mk5.gdx.fireapp.database.OrderByMode
import pl.mk5.gdx.fireapp.promises.Promise
import se.battlegoo.battlegoose.datamodels.ActionData
import se.battlegoo.battlegoose.datamodels.BattleData
import se.battlegoo.battlegoose.datamodels.LobbyData
import java.util.function.BiConsumer
import java.util.function.Consumer

typealias ConversionFunc<T> = (Map<String, Any>) -> T
typealias ListConversionFunc<T> = (List<Map<String, Any>>) -> List<T>
typealias ListenerCanceler = () -> Unit

class DatabaseHandler {

    private val setupPromise: Promise<GdxFirebaseUser>
    private lateinit var user: GdxFirebaseUser
    private var loaded = false

    init {
        setupPromise = signInAnonymously().then<GdxFirebaseUser> {
            user = it
            loaded = true
        }
    }

    private fun signInAnonymously(): Promise<GdxFirebaseUser> {
        return GdxFIRAuth.inst().signInAnonymously()
    }

    /**
     * Wrapper ensuring the database is connected
     */
    fun dbAccessWrapper(listener: () -> Unit) {
        if (loaded) {
            listener()
        } else {
            setupPromise.then<GdxFirebaseUser> { listener() }
        }
    }

    fun getUserID(consumer: Consumer<String>) {
        dbAccessWrapper {
            consumer.accept(user.userInfo.uid)
        }
    }

    inline fun <reified T : Any> readFilteredValue(
        databasePath: String,
        order: OrderByMode,
        orderByArg: String,
        filter: FilterType,
        filterArg: String,
        noinline fail: (String, Throwable) -> Unit = { _, throwable -> throw throwable },
        consumer: Consumer<T?>,
    ) {
        dbAccessWrapper {
            GdxFIRDatabase.inst()
                .inReference(databasePath)
                .filter(filter, filterArg)
                .orderBy(order, orderByArg)
                .readValue(T::class.java)
                .then<T> { consumer.accept(it) }
                .fail(fail)
        }
    }

    inline fun <reified T : Any> listenPrimitiveValue(
        databasePath: String,
        noinline listenerCancelerConsumer: (ListenerCanceler) -> Unit = { _ -> },
        noinline fail: (String, Throwable) -> Unit = { _, throwable -> throw throwable },
        consumer: BiConsumer<T?, ListenerCanceler>
    ) {
        dbAccessWrapper {
            val listener =
                GdxFIRDatabase.inst().inReference(databasePath).onDataChange(T::class.java)
            listener.then<T> {
                consumer.accept(it, listener::cancel)
            }.fail(fail)
            listenerCancelerConsumer.invoke(listener::cancel)
        }
    }

    inline fun <reified T : Any> listenListValue(
        databasePath: String,
        noinline listenerCancelerConsumer: (ListenerCanceler) -> Unit = { _ -> },
        noinline fail: (String, Throwable) -> Unit = { _, throwable -> throw throwable },
        consumer: BiConsumer<List<T>?, ListenerCanceler>,
    ) {
        @Suppress("UNCHECKED_CAST")
        when (T::class) {
            ActionData::class -> listenListDataClass(
                databasePath,
                this::convertToActionDataList as ListConversionFunc<T>,
                fail, listenerCancelerConsumer, consumer
            )
        }
    }

    inline fun <reified T : Any> readPrimitiveValue(
        databasePath: String,
        noinline fail: (String, Throwable) -> Unit = { _, throwable -> throw throwable },
        consumer: Consumer<T?>
    ) {
        dbAccessWrapper {
            GdxFIRDatabase.inst().inReference(databasePath).readValue(T::class.java).then<T> {
                consumer.accept(it)
            }.fail(fail)
        }
    }

    inline fun <reified T : Any> readReferenceValue(
        databasePath: String,
        noinline fail: (String, Throwable) -> Unit = { _, throwable -> throw throwable },
        consumer: Consumer<T?>
    ) {
        @Suppress("UNCHECKED_CAST")
        when (T::class) {
            LobbyData::class ->
                readDataClass(
                    databasePath, fail, consumer,
                    this::convertToLobby as
                        ConversionFunc<T>
                )
            BattleData::class ->
                readDataClass(
                    databasePath, fail, consumer,
                    this::convertToBattle as ConversionFunc<T>
                )
        }
    }

    fun <T> readDataClass(
        databasePath: String,
        fail: (String, Throwable) -> Unit = { _, throwable -> throw throwable },
        consumer: Consumer<T?>,
        conversionFunc: ConversionFunc<T>
    ) {
        return readPrimitiveValue<Map<String, Any>>(
            databasePath, fail
        ) { consumerData ->
            if (consumerData == null) {
                consumer.accept(null)
            } else {
                consumer.accept(conversionFunc(consumerData))
            }
        }
    }

    fun <T> listenListDataClass(
        databasePath: String,
        listConversionFunc: ListConversionFunc<T>,
        fail: (String, Throwable) -> Unit = { _, throwable -> throw throwable },
        listenerCancelerConsumer: (ListenerCanceler) -> Unit = { _ -> },
        consumer: BiConsumer<List<T>?, ListenerCanceler>
    ) {
        listenPrimitiveValue<List<Map<String, Any>>>(
            databasePath, listenerCancelerConsumer, fail
        ) { consumerData, canceler ->
            if (consumerData == null) {
                consumer.accept(null, canceler)
            } else {
                consumer.accept(listConversionFunc(consumerData), canceler)
            }
        }
    }

    fun setValue(
        databasePath: String,
        value: Any,
        fail: (String, Throwable) -> Unit = { _, throwable -> throw throwable },
        callback: () -> Unit
    ) {
        dbAccessWrapper {
            GdxFIRDatabase.inst()
                .inReference(databasePath)
                .setValue(value)
                .then<Void> { callback() }
                .fail(fail)
        }
    }

    fun pushValue(
        databasePath: String,
        value: Any,
        fail: (String, Throwable) -> Unit = { _, throwable -> throw throwable },
        callback: () -> Unit
    ) {
        dbAccessWrapper {
            GdxFIRDatabase.inst()
                .inReference(databasePath)
                .push()
                .setValue(value)
                .then<Void> { callback() }
                .fail(fail)
        }
    }

    fun deleteValue(
        databasePath: String,
        fail: (String, Throwable) -> Unit = { _, throwable -> throw throwable },
        callback: () -> Unit
    ) {
        dbAccessWrapper {
            GdxFIRDatabase.inst()
                .inReference(databasePath)
                .removeValue()
                .then<Void> { callback() }
                .fail(fail)
        }
    }

    fun convertToLobby(data: Map<String, Any>): LobbyData {
        val lobbyID = data[LobbyData::lobbyID.name] as String
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
