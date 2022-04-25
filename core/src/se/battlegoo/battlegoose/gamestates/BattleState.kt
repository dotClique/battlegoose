package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.controllers.BattleController
import se.battlegoo.battlegoose.datamodels.GridVector
import se.battlegoo.battlegoose.datamodels.ScreenVector
import se.battlegoo.battlegoose.models.Battle
import se.battlegoo.battlegoose.models.BattleMap
import se.battlegoo.battlegoose.models.BattleMapBackground
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.views.BattleView

class BattleState(
    playerID: String,
    battleID: String,
    hostHero: Hero,
    otherHero: Hero,
    isHost: Boolean
) : GameState() {

    private val battle = Battle(
        if (isHost) hostHero else otherHero,
        if (isHost) otherHero else hostHero,
        BattleMap(
            BattleMapBackground.values().random(BattleController.getRandom(battleID)),
            GridVector(10, 6)
        ),
        battleID,
        isHost
    )

    private val battleController: BattleController = BattleController(
        battle,
        BattleView(
            battle.battleMap.background,
            ScreenVector(0f, 0f),
            ScreenVector(Game.WIDTH, Game.HEIGHT),
            battle.hero1,
            battle.hero2,
            stage
        ),
        playerID
    )

    override fun update(dt: Float) = battleController.update(dt)

    override fun render(sb: SpriteBatch) = battleController.render(sb)

    override fun dispose() = battleController.dispose()
}
