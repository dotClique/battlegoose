package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.GridVector
import se.battlegoo.battlegoose.ScreenVector
import se.battlegoo.battlegoose.controllers.BattleMapController
import se.battlegoo.battlegoose.controllers.UnitController
import se.battlegoo.battlegoose.models.Battle
import se.battlegoo.battlegoose.models.BattleMap
import se.battlegoo.battlegoose.models.BattleMapBackground
import se.battlegoo.battlegoose.models.Obstacle
import se.battlegoo.battlegoose.models.heroes.SergeantSwan
import se.battlegoo.battlegoose.models.units.DelinquentDuck
import se.battlegoo.battlegoose.models.units.GuardGoose
import se.battlegoo.battlegoose.models.units.PrivatePenguin
import se.battlegoo.battlegoose.models.units.SpitfireSeagull
import se.battlegoo.battlegoose.views.BattleMapView
import se.battlegoo.battlegoose.views.FacingDirection
import se.battlegoo.battlegoose.views.UnitSprite
import se.battlegoo.battlegoose.views.UnitView
import kotlin.random.Random

class BattleState : GameState() {

    private val mapSize = ScreenVector(
        Game.WIDTH * 0.8f,
        Game.HEIGHT * 0.9f
    )

    private val battle = Battle(
        SergeantSwan(),
        SergeantSwan(),
        BattleMap(BattleMapBackground.values().random(), GridVector(10, 6))
    )

    private val battleMapController = BattleMapController(
        battle.hero1,
        battle.battleMap,
        BattleMapView(
            battle.battleMap.background,
            ScreenVector((Game.WIDTH - mapSize.x) / 2f, (Game.HEIGHT - mapSize.y) / 2f),
            mapSize
        )
    )

    init {
        for (y in 0 until battleMapController.mapSize.y) {
            for (x in arrayOf(0, battleMapController.mapSize.x - 1 - (y % 2))) {
                val hero = if (x == 0) battle.hero1 else battle.hero2
                val direction = if (x == 0) FacingDirection.RIGHT else FacingDirection.LEFT
                val controller = when (Random.nextInt(4)) {
                    3 -> UnitController(
                        GuardGoose(hero), UnitView(UnitSprite.GUARD_GOOSE, direction)
                    )
                    2 -> UnitController(
                        DelinquentDuck(hero), UnitView(UnitSprite.DELINQUENT_DUCK, direction)
                    )
                    1 -> UnitController(
                        PrivatePenguin(hero), UnitView(UnitSprite.PRIVATE_PENGUIN, direction)
                    )
                    else -> UnitController(
                        SpitfireSeagull(hero), UnitView(UnitSprite.SPITFIRE_SEAGULL, direction)
                    )
                }
                battleMapController.addUnit(controller, GridVector(x, y))
            }
        }

        // Place obstacles randomly
        (0 until battleMapController.mapSize.y)
            .map { y ->
                (2 until battleMapController.mapSize.x - 3)
                    .map { x -> GridVector(x, y) }
                    .filter { oPos ->
                        battle.battleMap.let { !it.isObstacleAt(oPos) && !it.isUnitAt(oPos) }
                    }
            }
            .flatten()
            .shuffled()
            .take(Random.nextInt(2, 5))
            .forEach { battleMapController.addObstacle(Obstacle.values().random(), it) }
    }

    override fun update(dt: Float) = battleMapController.update(dt)

    override fun render(sb: SpriteBatch) = battleMapController.render(sb)

    override fun dispose() = battleMapController.dispose()
}
