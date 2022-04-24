package se.battlegoo.battlegoose.network

import pl.mk5.gdx.fireapp.GdxFIRAuth
import pl.mk5.gdx.fireapp.GdxFIRDatabase
import pl.mk5.gdx.fireapp.auth.GdxFirebaseUser
import pl.mk5.gdx.fireapp.database.FilterType
import pl.mk5.gdx.fireapp.database.OrderByMode
import pl.mk5.gdx.fireapp.promises.Promise
import se.battlegoo.battlegoose.datamodels.ActionData
import se.battlegoo.battlegoose.datamodels.BattleData
import se.battlegoo.battlegoose.datamodels.DataModel
import se.battlegoo.battlegoose.datamodels.GridVector
import se.battlegoo.battlegoose.datamodels.LobbyData
import se.battlegoo.battlegoose.datamodels.RandomOpponentData
import se.battlegoo.battlegoose.datamodels.SpellData
import java.util.function.BiConsumer
import java.util.function.Consumer

typealias ConversionFunc<T> = (Map<String, Any>) -> T
typealias ListenerCanceler = () -> Unit
typealias FailFunc = (String, Throwable) -> Unit

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

    inline fun <reified T : Any> readFilteredPrimitiveList(
        databasePath: PrimitiveListDbPath<T>,
        order: OrderByMode,
        orderByArg: String,
        filter: FilterType,
        filterArg: String,
        noinline onFail: FailFunc = { _, throwable -> throw throwable },
        consumer: Consumer<List<T>?>,
    ) {
        dbAccessWrapper {
            GdxFIRDatabase.inst()
                .inReference(databasePath.path)
                .filter(filter, filterArg)
                .orderBy(order, orderByArg)
                .readValue(List::class.java)
                .then<List<T>?> { consumer.accept(it) }
                .fail(onFail)
        }
    }

    inline fun <reified T : Any> listen(
        databasePath: DbPath<T>,
        noinline onFail: FailFunc = { _, throwable -> throw throwable },
        noinline listenerCancelerConsumer: (ListenerCanceler) -> Unit = { _ -> },
        consumer: BiConsumer<T?, ListenerCanceler>
    ) {
        dbAccessWrapper {
            val listener =
                GdxFIRDatabase.inst().inReference(databasePath.path).onDataChange(T::class.java)
            listener.then<T?> {
                consumer.accept(it, listener::cancel)
            }.fail(onFail)
            listenerCancelerConsumer.invoke(listener::cancel)
        }
    }

    inline fun <reified T : DataModel> listen(
        databasePath: DataModelDbPath<T>,
        noinline listenerCancelerConsumer: (ListenerCanceler) -> Unit = { _ -> },
        noinline onFail: FailFunc = { _, throwable -> throw throwable },
        consumer: BiConsumer<T?, ListenerCanceler>
    ) {
        listen(
            databasePath.toPrimitive(),
            onFail,
            listenerCancelerConsumer
        ) { value, canceler ->
            consumer.accept(value?.let(getConverter()), canceler)
        }
    }

    inline fun <reified T : DataModel> listen(
        databasePath: DataModelListDbPath<T>,
        noinline onFail: FailFunc = { _, throwable -> throw throwable },
        noinline listenerCancelerConsumer: (ListenerCanceler) -> Unit = { _ -> },
        consumer: BiConsumer<List<T>?, ListenerCanceler>
    ) {
        val converter = getConverter<T>()
        listen(
            databasePath.toPrimitive(),
            onFail,
            listenerCancelerConsumer
        ) { list, canceler ->
            consumer.accept(list?.map(converter), canceler)
        }
    }

    inline fun <reified T : DataModel> listen(
        databasePath: DataModelMapDbPath<T>,
        noinline onFail: FailFunc = { _, throwable -> throw throwable },
        noinline listenerCancelerConsumer: (ListenerCanceler) -> Unit = { _ -> },
        consumer: BiConsumer<Map<String, T>?, ListenerCanceler>
    ) {
        val converter = getConverter<T>()
        listen(
            databasePath.toPrimitive(),
            onFail,
            listenerCancelerConsumer
        ) { map, canceler ->
            consumer.accept(map?.map { Pair(it.key, converter(it.value)) }?.toMap(), canceler)
        }
    }

    inline fun <reified T : Any> read(
        databasePath: PrimitiveDbPath<T>,
        noinline onFail: FailFunc = { _, throwable -> throw throwable },
        consumer: Consumer<T?>
    ) {
        dbAccessWrapper {
            GdxFIRDatabase.inst()
                .inReference(databasePath.path)
                .readValue(T::class.java)
                .then(consumer::accept)
                .fail(onFail)
        }
    }

    inline fun <reified T : DataModel> read(
        databasePath: DataModelDbPath<T>,
        noinline onFail: FailFunc = { _, throwable -> throw throwable },
        consumer: Consumer<T?>
    ) {
        read(databasePath.toPrimitive(), onFail) {
            consumer.accept(it?.let(getConverter()))
        }
    }

    inline fun <reified T : DataModel> read(
        databasePath: DataModelListDbPath<T>,
        noinline onFail: FailFunc = { _, throwable -> throw throwable },
        consumer: Consumer<List<T>?>
    ) {
        val converter = getConverter<T>()
        read(databasePath.toPrimitive(), onFail) {
            consumer.accept(it?.map(converter))
        }
    }

    inline fun <reified T : DataModel> read(
        databasePath: DataModelMapDbPath<T>,
        noinline onFail: FailFunc = { _, throwable -> throw throwable },
        consumer: Consumer<Map<String, T>?>
    ) {
        val converter = getConverter<T>()
        read(databasePath.toPrimitive(), onFail) { map ->
            consumer.accept(map?.map { Pair(it.key, converter(it.value)) }?.toMap())
        }
    }

    fun <T> setValue(
        databasePath: DbPath<T>,
        value: T,
        onFail: FailFunc = { _, throwable -> throw throwable },
        onSuccess: () -> Unit
    ) {
        dbAccessWrapper {
            GdxFIRDatabase.inst()
                .inReference(databasePath.path)
                .setValue(value)
                .then<Void> { onSuccess() }
                .fail(onFail)
        }
    }

    fun <T> deleteValue(
        databasePath: DbPath<T>,
        onFail: FailFunc = { _, throwable -> throw throwable },
        onSuccess: () -> Unit
    ) {
        dbAccessWrapper {
            GdxFIRDatabase.inst()
                .inReference(databasePath.path)
                .removeValue()
                .then<Void> { onSuccess() }
                .fail(onFail)
        }
    }

    inline fun <reified T : DataModel> getConverter(): ConversionFunc<T> {
        @Suppress("UNCHECKED_CAST")
        return when (T::class) {
            LobbyData::class -> ::convertToLobby
            ActionData::class -> ::convertToActionData
            SpellData::class -> ::convertToSpellData
            BattleData::class -> ::convertToBattle
            RandomOpponentData::class -> ::convertToRandomOpponentData
            else -> throw NotImplementedError("No deserializer for ${T::class}")
        } as ConversionFunc<T>
    }

    fun convertToLobby(data: Map<String, Any>): LobbyData {
        val lobbyID = data[LobbyData::lobbyID.name] as String
        val hostID = data[LobbyData::hostID.name] as String
        val otherPlayerID = data[LobbyData::otherPlayerID.name] as String
        val battleID = data[LobbyData::battleID.name] as String
        return LobbyData(lobbyID, hostID, otherPlayerID, battleID)
    }

    @Suppress("UNCHECKED_CAST")
    fun convertToBattle(battleData: Map<String, Any>): BattleData {
        val battleID = battleData[BattleData::battleID.name] as String
        val hostID = battleData[BattleData::hostID.name] as String
        val otherPlayerID = battleData[BattleData::otherPlayerID.name] as String

        val actionsRaw = battleData[BattleData::actions.name] as List<Map<String, Any>>?
        val actions: List<ActionData> = actionsRaw
            ?.map(::convertToActionData)
            ?: emptyList()
        return BattleData(battleID, hostID, otherPlayerID, actions)
    }

    @Suppress("UNCHECKED_CAST")
    fun convertToActionData(actionData: Map<String, Any>): ActionData {
        val playerID = actionData[ActionData::playerID.name] as String
        val actionType = actionData[ActionData::actionType.name] as String
        val actionPointCost = (actionData[ActionData::actionPointCost.name] as Long).toInt()
        // Parse a string like "se.battlegoo.battlegoose.ActionData$MoveUnit" into a KClass
        // MUST have a branch for each subclass of ActionData
        return when (Class.forName(actionType).kotlin) {
            ActionData.MoveUnit::class -> ActionData.MoveUnit(
                playerID,
                convertToGridVector(
                    actionData[ActionData.MoveUnit::fromPosition.name] as Map<String, Any>
                ),
                convertToGridVector(
                    actionData[ActionData.MoveUnit::toPosition.name] as Map<String, Any>
                ),
                actionPointCost
            )
            ActionData.AttackUnit::class -> ActionData.AttackUnit(
                playerID,
                convertToGridVector(
                    actionData[ActionData.AttackUnit::attackerPosition.name] as Map<String, Any>
                ),
                convertToGridVector(
                    actionData[ActionData.AttackUnit::targetPosition.name] as Map<String, Any>
                ),
                actionPointCost
            )
            ActionData.CastSpell::class -> ActionData.CastSpell(
                playerID,
                convertToSpellData(actionData),
                actionPointCost
            )
            ActionData.Pass::class -> ActionData.Pass(playerID)
            ActionData.Forfeit::class -> ActionData.Forfeit(playerID)
            else -> throw NotImplementedError("The action class $actionType has no deserializer")
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun convertToSpellData(data: Map<String, Any>): SpellData {
        val spellData = data[ActionData.CastSpell<*>::spell.name] as Map<String, Any>
        val spellType = spellData[SpellData::spellType.name] as String
        // Parse a string like "se.battlegoo.battlegoose.SpellData$AdrenalineShotSpellData" into a
        // KClass
        // MUST have a branch for each subclass of SpellData
        return when (Class.forName(spellType).kotlin) {
            SpellData.AdrenalineShot::class -> SpellData.AdrenalineShot
            SpellData.Bird52::class -> SpellData.Bird52
            SpellData.EphemeralAllegiance::class -> SpellData.EphemeralAllegiance(
                convertToGridVector(
                    spellData[SpellData.EphemeralAllegiance::targetPosition.name]
                        as Map<String, Any>
                )
            )
            else -> throw NotImplementedError("The spell class $spellType has no deserializer")
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun convertToRandomOpponentData(data: Map<String, Any>): RandomOpponentData {
        val availabeLobby = data[RandomOpponentData::availableLobby.name] as String
        val lastUpdated = data[RandomOpponentData::lastUpdated.name] as Long
        return RandomOpponentData(availabeLobby, lastUpdated)
    }

    private fun convertToGridVector(data: Map<String, Any>): GridVector {
        return GridVector(
            (data[GridVector::x.name] as Long).toInt(),
            (data[GridVector::y.name] as Long).toInt()
        )
    }
}
