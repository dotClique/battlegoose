package se.battlegoo.battlegoose.datamodels

data class BattleData(
    val battleID: String,
    val hostID: String,
    val otherPlayerID: String,
    val actions: List<ActionData>,
    val hostHero: HeroData<*>,
    val otherHero: HeroData<*>?
) : DataModel
