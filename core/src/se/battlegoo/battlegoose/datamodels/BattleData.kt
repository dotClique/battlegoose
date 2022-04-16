package se.battlegoo.battlegoose.datamodels

data class BattleData(
    val battleID: String,
    val hostID: String,
    val otherPlayerID: String,
    val actions: List<ActionData>
    // Also store starting positions and starting map here
)
