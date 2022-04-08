package se.battlegoo.battlegoose.datamodels

import se.battlegoo.battlegoose.datamodels.ActionData

data class BattleData(
        val hostID: String,
        val otherPlayerID: String,
        val actions: List<ActionData>
	// Also store starting positions and starting map here
)

