package se.battlegoo.battlegoose.network

import se.battlegoo.battlegoose.datamodels.BattleData
import se.battlegoo.battlegoose.datamodels.DataModel
import se.battlegoo.battlegoose.datamodels.LobbyData
import kotlin.reflect.KProperty1

sealed class DbPath<T>(val path: String) {
    // Schema of all root paths (further traversal by []-operator)
    object Lobbies : DataModelMapDbPath<LobbyData>("LOBBIES")
    object Battles : DataModelMapDbPath<BattleData>("BATTLES")
    object RandomOpponentQueue : PrimitiveListDbPath<String>("RANDOM_OPPONENT_QUEUE")
    object Username : PrimitiveMapDbPath<String>("USERNAME")
    object Leaderboard : PrimitiveMapDbPath<Long>("LEADERBOARD")
    object RandomOpponentData :
        DataModelDbPath<se.battlegoo.battlegoose.datamodels.RandomOpponentData>
        ("RANDOM_OPPONENT_DATA")
}

/*  Table of all the possible DbPath-types

             |   Primitive             |   DataModel
    _________|_________________________|______________________
    Single   |   PrimitiveDbPath       |   DataModelDbPath
    List     |   PrimitiveListDbPath   |   DataModelListDbPath
    Map      |   PrimitiveMapDbPath    |   DataModelMapDbPath
 */

// Single

open class PrimitiveDbPath<T>(path: String) : DbPath<T>(path)

open class DataModelDbPath<T : DataModel>(path: String) : DbPath<T>(path) {

    operator fun <S> get(property: KProperty1<in T, S>): PrimitiveDbPath<S> {
        return PrimitiveDbPath("$path/${property.name}")
    }

    operator fun <S : DataModel> get(property: KProperty1<in T, S>): DataModelDbPath<S> {
        return DataModelDbPath("$path/${property.name}")
    }

    operator fun <S : DataModel> get(
        property: KProperty1<in T, Map<String, S>>
    ): DataModelMapDbPath<S> {
        return DataModelMapDbPath("$path/${property.name}")
    }

    operator fun <S : DataModel> get(property: KProperty1<in T, List<S>>): DataModelListDbPath<S> {
        return DataModelListDbPath("$path/${property.name}")
    }

    fun toPrimitive(): PrimitiveMapDbPath<Any> {
        return PrimitiveMapDbPath(path)
    }
}

// List

open class PrimitiveListDbPath<T>(path: String) : PrimitiveDbPath<List<T>>(path) {
    operator fun get(key: String): PrimitiveDbPath<T> {
        return PrimitiveDbPath("$path/$key")
    }
}

open class DataModelListDbPath<T : DataModel>(path: String) : DbPath<List<T>>(path) {

    operator fun get(index: Long): DataModelDbPath<T> {
        return DataModelDbPath("$path/$index")
    }

    fun toPrimitive(): PrimitiveListDbPath<Map<String, Any>> {
        return PrimitiveListDbPath(path)
    }
}

// Map

open class PrimitiveMapDbPath<T>(path: String) : PrimitiveDbPath<Map<String, T>>(path) {
    operator fun get(key: String): PrimitiveDbPath<T> {
        return PrimitiveDbPath("$path/$key")
    }
}

open class DataModelMapDbPath<T : DataModel>(path: String) :
    DbPath<Map<String, T>>(path) {
    operator fun get(key: String): DataModelDbPath<T> {
        return DataModelDbPath("$path/$key")
    }

    fun toPrimitive(): PrimitiveMapDbPath<Map<String, Any>> {
        return PrimitiveMapDbPath(path)
    }
}
