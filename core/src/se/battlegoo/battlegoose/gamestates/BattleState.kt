package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.controllers.BattleController
import se.battlegoo.battlegoose.datamodels.GridVector
import se.battlegoo.battlegoose.models.Battle
import se.battlegoo.battlegoose.models.BattleMap
import se.battlegoo.battlegoose.models.BattleMapBackground
import se.battlegoo.battlegoose.models.heroes.SergeantSwan
import se.battlegoo.battlegoose.views.FacingDirection
import se.battlegoo.battlegoose.views.UnitSprite
import se.battlegoo.battlegoose.views.UnitView

class BattleState(playerID: String, battleID: String, isHost: Boolean) : GameState() {
    private val battleController: BattleController = BattleController(
        Battle(
            SergeantSwan(),
            SergeantSwan(),
            BattleMap(
                BattleMapBackground.values().random(BattleController.getRandom(battleID)),
                GridVector(10, 6)
            ),
            battleID,
            isHost
        ),
        UnitView(UnitSprite.DELINQUENT_DUCK, FacingDirection.LEFT), // TODO: Change to
        // BattleView
        playerID
    )

    override fun update(dt: Float) = battleController.update(dt)

    override fun render(sb: SpriteBatch) = battleController.render(sb)

    override fun dispose() = battleController.dispose()
}
