package se.battlegoo.battlegoose.network

import se.battlegoo.battlegoose.datamodels.ActionData
import se.battlegoo.battlegoose.datamodels.LobbyData
import se.battlegoo.battlegoose.datamodels.RandomOpponentData
import se.battlegoo.battlegoose.datamodels.SpellData
import kotlin.random.Random

object TestDatabaseHandler {
    private val databaseHandler = DatabaseHandler()

    fun testSetDataModel() {
        databaseHandler.setValue(
            DataModelDbPath<ActionData>("Larstest"),
            ActionData.CastSpell("john", SpellData.AdrenalineShotSpellData, 3)
        ) {
            println("Set")
            PrimitiveDbPath<String>("")
        }
    }

    fun testReadDataModel() {
        databaseHandler.readDataModel(DataModelDbPath<ActionData>("Larstest")) {
            println("Got $it")
        }
    }

    fun testReadPrimitiveList() {
        databaseHandler.readPrimitive(PrimitiveListDbPath<List<String>>("Larstest")) {
            println("Got $it")
        }
    }

    fun testSetPrimitiveList() {
        databaseHandler.setValue(
            PrimitiveListDbPath("Larstest"),
            listOf("Hello", "There", "World")
        ) {
            println("Set")
        }
    }

    fun testReadDataModelList() {
        databaseHandler.readDataModel(DataModelListDbPath<RandomOpponentData>("Larstest")) {
            println("Got $it")
        }
    }

    fun testSetDataModelList() {
        databaseHandler.setValue(
            DataModelListDbPath<RandomOpponentData>("Larstest"),
            listOf(
                RandomOpponentData("asdf", 2),
                RandomOpponentData("sf", 4)
            )
        ) {
            println("Set")
        }
    }

    fun testSetDataModelRandom() {
        databaseHandler.setValue(
            DataModelDbPath<RandomOpponentData>("Larstest"),
            RandomOpponentData("", Random.nextLong())
        ) {
            println("Set")
        }
    }

    fun testListenDataModel() {
        databaseHandler.listenDataModel(DataModelDbPath<RandomOpponentData>("Larstest")) { it, _ ->
            println("Got $it")
        }
    }

    fun testSetDataModelListRandom() {
        databaseHandler.setValue(
            DataModelListDbPath<RandomOpponentData>("Larstest"),
            listOf(RandomOpponentData("", Random.nextLong()))
        ) {
            println("Set")
        }
    }

    fun testListenDataModelList() {
        databaseHandler.listenDataModel(
            DataModelListDbPath<RandomOpponentData>("Larstest")
        ) { it, _ ->
            println("Got $it")
        }
    }

    fun testSetDataModelMapRandom() {
        databaseHandler.setValue(
            DataModelMapDbPath<RandomOpponentData>("Larstest"),
            mapOf(
                Pair("hei", RandomOpponentData("", Random.nextLong())),
                Pair("verden", RandomOpponentData("a", Random.nextLong())),
            )
        ) {
            println("Set")
        }
    }

    fun testListenDataModelMap() {
        databaseHandler.listenDataModel(
            DataModelMapDbPath<RandomOpponentData>("Larstest")
        ) { it, _ ->
            println("Got $it")
        }
    }

    fun testReadDataModelProperty() {
        databaseHandler.readPrimitive(DbPath.RandomOpponentData[RandomOpponentData::lastUpdated]) {
            println("Got $it")
        }
    }

    fun testReadNestedProperty() {
        databaseHandler.readPrimitive(DbPath.Lobbies["AFRCIL"][LobbyData::hostID]) {
            println("Got $it")
        }
    }
}
