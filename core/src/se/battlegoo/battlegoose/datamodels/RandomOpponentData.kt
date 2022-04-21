package se.battlegoo.battlegoose.datamodels

data class RandomOpponentData(
    val availableLobby: String,
    val lastUpdated: Long
) : DataModel
