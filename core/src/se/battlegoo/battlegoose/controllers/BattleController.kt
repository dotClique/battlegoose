package se.battlegoo.battlegoose.controllers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Logger
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.datamodels.ActionData
import se.battlegoo.battlegoose.datamodels.GridVector
import se.battlegoo.battlegoose.datamodels.ScreenVector
import se.battlegoo.battlegoose.datamodels.SpellData
import se.battlegoo.battlegoose.gamestates.GameStateManager
import se.battlegoo.battlegoose.models.Battle
import se.battlegoo.battlegoose.models.BattleOutcome
import se.battlegoo.battlegoose.models.Obstacle
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.models.heroes.HeroStatsModifier
import se.battlegoo.battlegoose.models.spells.ActiveSpell
import se.battlegoo.battlegoose.models.spells.AdrenalineShotSpell
import se.battlegoo.battlegoose.models.spells.Bird52Spell
import se.battlegoo.battlegoose.models.spells.EphemeralAllegianceSpell
import se.battlegoo.battlegoose.models.units.DelinquentDuck
import se.battlegoo.battlegoose.models.units.GuardGoose
import se.battlegoo.battlegoose.models.units.PrivatePenguin
import se.battlegoo.battlegoose.models.units.SpitfireSeagull
import se.battlegoo.battlegoose.network.MultiplayerService
import se.battlegoo.battlegoose.utils.Modal
import se.battlegoo.battlegoose.utils.ModalType
import se.battlegoo.battlegoose.views.BattleMapView
import se.battlegoo.battlegoose.views.FacingDirection
import se.battlegoo.battlegoose.views.UnitSprite
import se.battlegoo.battlegoose.views.UnitView
import kotlin.IllegalStateException
import kotlin.math.abs
import kotlin.random.Random

class BattleController(
    val battle: Battle,
    private val view: UnitView,
    private val playerID: String
) : ControllerBase(view) {

    companion object {
        fun getRandom(battleId: String): Random {
            return Random(battleId.hashCode())
        }
    }

    private val random: Random = getRandom(battle.battleId)

    private val mapSize = ScreenVector(
        Game.WIDTH * 0.8f,
        Game.HEIGHT * 0.9f
    )

    private val battleMapController = BattleMapController(
        battle.hero1,
        battle.battleMap,
        BattleMapView(
            battle.battleMap.background,
            ScreenVector((Game.WIDTH - mapSize.x) / 2f, (Game.HEIGHT - mapSize.y) / 2f),
            mapSize
        ),
        ::onAttackUnit,
        ::onMoveUnit
    )

    init {
        view.position = ScreenVector(50f, 50f)
        view.size = ScreenVector(200f, 200f)
        placeUnits()
        placeObstacles()
        battle.yourTurn = random.nextBoolean() xor battle.isHost
        Logger(Game.LOGGER_TAG, Logger.INFO).info("Battle ${battle.battleId} playerId: $playerID")
        startTurn()
    }

    override fun render(sb: SpriteBatch) {
        battleMapController.render(sb)
        view.render(sb)
    }

    override fun update(dt: Float) {
        // Example spell cast button
        if (Gdx.input.justTouched()) {
            val touchPoint = Game.unproject(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
            if (Rectangle(view.position.x, view.position.y, view.size.x, view.size.y)
                .contains(touchPoint)
            ) {
                onCastSpell()
                view.position = ScreenVector(view.position.x, view.position.y + view.size.y)
            }
        }

        if (battle.yourTurn) {
            battleMapController.yourTurn = true
        } else {
            battleMapController.yourTurn = false
            checkForOpposingMoves()
        }
        battleMapController.update(dt)
    }

    override fun dispose() {
        battleMapController.dispose()
    }

    private fun startTurn() {
        Logger(Game.LOGGER_TAG, Logger.INFO)
            .info((if (battle.yourTurn) "My" else "Opponent's") + " turn")
        applySpells(if (battle.yourTurn) battle.activeSpells.first else battle.activeSpells.second)
    }

    private fun endTurn() {
        battle.getCurrentOutcome()?.let(::resolveGame)
        battle.nextTurn()
        startTurn()
    }

    private fun resolveGame(outcome: BattleOutcome) {
        MultiplayerService.endBattle()
        MultiplayerService.incrementScore(outcome.scoreChange) {}
        // view.showResolutionScreen { GameStateManager.goBack() }

        // TODO: Move to BattleView
        val (title, text) = when (outcome) {
            BattleOutcome.VICTORY -> Pair("VICTORY!", "You are victorious!\nFantastic job!")
            BattleOutcome.TIE -> Pair("TIE!", "It's a tie!\nIt could have been worse.")
            BattleOutcome.DEFEAT -> Pair("DEFEAT!", "You lost!\nBetter luck next time!")
        }
        val pointChange = outcome.scoreChange
        val pluralS = if (abs(pointChange) == 1L) "" else "s"
        val pointMessage = when {
            pointChange > 0L -> "You have gained $pointChange point$pluralS."
            pointChange < 0L -> "Your have lost ${abs(pointChange)} point$pluralS."
            else -> "Your score is unchanged."
        }
        Modal(
            title,
            text + "\n" + pointMessage,
            ModalType.Info { GameStateManager.goBack() }
            // ModalType.Info(onResolutionAccepted)
        ).show()
    }

    private fun applySpells(activeSpells: MutableList<ActiveSpell<*>>) {
        val spellsIterator = activeSpells.iterator()
        while (spellsIterator.hasNext()) {
            val spell = spellsIterator.next()
            println(
                "Applying spell $spell, hero1 ${battle.hero1.currentStats.actionPoints} " +
                    "${battle.hero2.currentStats.actionPoints}"
            )
            spell.apply(battle)
            println(
                "Applied spell, hero1 ${battle.hero1.currentStats.actionPoints} " +
                    "${battle.hero2.currentStats.actionPoints}"
            )
            if (spell.finished) {
                spellsIterator.remove()
            }
        }
    }

    private fun checkForOpposingMoves() {
        val newActions = MultiplayerService.readActionDataBuffer() ?: return
        newActions.filter { it.playerID != playerID }.forEach(::processReceivedAction)
    }

    private fun processReceivedAction(action: ActionData) {
        when (action) {
            is ActionData.AttackUnit -> handleReceivedAttackAction(action)
            is ActionData.Forfeit -> resolveGame(BattleOutcome.VICTORY)
            is ActionData.MoveUnit -> handleReceivedMoveAction(action)
            is ActionData.Pass -> handleReceivedPassAction()
            is ActionData.CastSpell<*> -> handleReceivedSpellCast(action.spell)
        }
        battle.actions += action
        subtractActionPoints(battle.hero2, action.actionPointCost)
    }

    private fun handleReceivedPassAction() {
        subtractActionPoints(battle.hero2, battle.hero2.currentStats.actionPoints)
    }

    private fun handleReceivedMoveAction(action: ActionData.MoveUnit) {
        val from = otherPerspective(action.fromPosition)
        val to = otherPerspective(action.toPosition)
        val unit = battle.battleMap.getUnit(from)
            ?: throw IllegalStateException("There is no unit at the received from pos $from")
        if (unit.allegiance != battle.hero2) {
            throw IllegalStateException(
                "The unit at received move from-position $to doesn't " +
                    "belong to the other player!"
            )
        }
        battleMapController.moveUnit(from, to)
    }

    private fun handleReceivedAttackAction(action: ActionData.AttackUnit) {
        val attackerPosition = otherPerspective(action.attackerPosition)
        val targetPosition = otherPerspective(action.targetPosition)
        val attackingUnit = battle.battleMap.getUnit(attackerPosition)
            ?: throw IllegalStateException(
                "There is no unit at the received attacker pos $attackerPosition"
            )
        val targetUnit = battle.battleMap.getUnit(targetPosition)
            ?: throw IllegalStateException(
                "There is no unit at the received target pos $targetPosition "
            )
        if (attackingUnit.allegiance != battle.hero2) {
            throw IllegalStateException(
                "The attacking unit at received position $attackerPosition " +
                    "doesn't belong to the other player!"
            )
        }
        if (targetUnit.allegiance != battle.hero1) {
            throw IllegalStateException(
                "The attacked unit at received position $targetPosition " +
                    "doesn't belong to this player!"
            )
        }
        battleMapController.attackUnit(attackingUnit, targetUnit)
    }

    private fun handleReceivedSpellCast(spellData: SpellData) {
        val spell = battle.hero2.spell
        val activeSpell = when (spellData) {
            is SpellData.AdrenalineShot -> {
                if (spell !is AdrenalineShotSpell)
                    throw IllegalStateException("Performed spell by other player isn't their spell")
                spell.cast(spellData)
            }
            is SpellData.EphemeralAllegiance -> {
                if (spell !is EphemeralAllegianceSpell)
                    throw IllegalStateException("Performed spell by other player isn't their spell")
                spell.cast(
                    spellData.copy(targetPosition = otherPerspective(spellData.targetPosition))
                )
            }
            is SpellData.Bird52 -> {
                if (spell !is Bird52Spell)
                    throw IllegalStateException("Performed spell by other player isn't their spell")
                spell.cast(spellData)
            }
        }
        battle.activeSpells.second.add(activeSpell)
    }

    private fun doAction(action: ActionData) {
        MultiplayerService.postAction(action)
        battle.actions += action
        subtractActionPoints(battle.hero1, action.actionPointCost)
    }

    private fun onAttackUnit(attackerPosition: GridVector, targetPosition: GridVector) {
        if (battle.yourTurn) {
            doAction(ActionData.MoveUnit(playerID, attackerPosition, targetPosition))
        }
    }

    private fun onMoveUnit(fromPosition: GridVector, toPosition: GridVector) {
        if (battle.yourTurn) {
            doAction(ActionData.AttackUnit(playerID, fromPosition, toPosition))
        }
    }

    private fun onCastSpell() {
        if (battle.yourTurn) {
            doAction(
                ActionData.CastSpell(
                    playerID,
                    when (battle.hero1.spell) {
                        is EphemeralAllegianceSpell -> {
                            val data = SpellData.EphemeralAllegiance(
                                battle.battleMap.getPosOfUnit(
                                    battle.battleMap.getUnits()
                                        .filter { it.allegiance == battle.hero2 }
                                        .random(random)
                                )!!
                            )
                            battle.activeSpells.first += battle.hero1.spell.cast(data)
                            data
                        }
                        is Bird52Spell -> {
                            val data = SpellData.Bird52
                            battle.activeSpells.first += battle.hero1.spell.cast(data)
                            data
                        }
                        is AdrenalineShotSpell -> {
                            val data = SpellData.AdrenalineShot
                            battle.activeSpells.first += battle.hero1.spell.cast(data)
                            data
                        }
                        else -> throw NotImplementedError(
                            "No casting implemented for ${battle.hero1.spell::class.java.name}"
                        )
                    }
                )
            )
        }
    }

    private fun onForfeit() {
        doAction(ActionData.Forfeit(playerID))
        resolveGame(BattleOutcome.DEFEAT)
    }

    private fun onPass() {
        doAction(ActionData.Pass(playerID))
        subtractActionPoints(battle.hero1, battle.hero1.currentStats.actionPoints)
    }

    private fun placeObstacles() {
        (0 until battleMapController.mapSize.y)
            .map { y ->
                (2 until battleMapController.mapSize.x - 3)
                    .map { x -> GridVector(x, y) }
                    .filter { oPos ->
                        battle.battleMap.let { !it.isObstacleAt(oPos) && !it.isUnitAt(oPos) }
                    }
            }
            .flatten()
            .shuffled(random)
            .take(random.nextInt(2, 5))
            .let { if (battle.isHost) it else it.map(::otherPerspective) }
            .forEach { battleMapController.addObstacle(Obstacle.values().random(random), it) }
    }

    private fun placeUnits() {
        val size = battle.battleMap.gridSize
        for (isThisPlayer in listOf(true, false)) {
            val hero = if (isThisPlayer) battle.hero1 else battle.hero2
            val direction = if (isThisPlayer) FacingDirection.RIGHT else FacingDirection.LEFT
            var tile: GridVector = GridVector(0, 0)

            for (unitClass in hero.army) {
                val controller = when (unitClass) {
                    GuardGoose::class -> UnitController(
                        GuardGoose(hero), UnitView(UnitSprite.GUARD_GOOSE, direction)
                    )
                    DelinquentDuck::class -> UnitController(
                        DelinquentDuck(hero), UnitView(UnitSprite.DELINQUENT_DUCK, direction)
                    )
                    PrivatePenguin::class -> UnitController(
                        PrivatePenguin(hero), UnitView(UnitSprite.PRIVATE_PENGUIN, direction)
                    )
                    SpitfireSeagull::class -> UnitController(
                        SpitfireSeagull(hero), UnitView(UnitSprite.SPITFIRE_SEAGULL, direction)
                    )
                    else -> throw NotImplementedError(
                        "No controller creator for unit class " +
                            unitClass.java.name
                    )
                }

                battleMapController.addUnit(
                    controller,
                    if (isThisPlayer) tile else otherPerspective(tile)
                )
                val wrap = tile.y == size.y - 1
                tile = if (wrap) GridVector(tile.x + 1, 0)
                else GridVector(tile.x, tile.y + 1)
            }
        }
    }

    private fun otherPerspective(pos: GridVector): GridVector {
        // Mirror GridVector horizontally around center axis of map to change from opponent's
        // perspective
        return GridVector(battleMapController.mapSize.x - 1 - (pos.y % 2) - pos.x, pos.y)
    }

    private fun subtractActionPoints(hero: Hero<*>, pointsToSubtract: Int) {
        hero.applyStatsModifier(
            HeroStatsModifier {
                it.copy(actionPoints = it.actionPoints - pointsToSubtract)
            }
        )
        if (hero.currentStats.actionPoints <= 0) {
            endTurn()
        }
    }
}
