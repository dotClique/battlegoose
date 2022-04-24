package se.battlegoo.battlegoose

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import se.battlegoo.battlegoose.datamodels.ActionData
import se.battlegoo.battlegoose.datamodels.GridVector
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.models.heroes.HeroSprite
import se.battlegoo.battlegoose.models.heroes.HeroStats
import se.battlegoo.battlegoose.models.heroes.HeroStatsModifier
import se.battlegoo.battlegoose.models.spells.AdrenalineShotSpell
import se.battlegoo.battlegoose.models.units.DelinquentDuck
import se.battlegoo.battlegoose.models.units.GuardGoose
import se.battlegoo.battlegoose.models.units.PrivatePenguin

class HeroTest {
    @Test
    fun testHero() {
        val hero = object : Hero<AdrenalineShotSpell>(
            HeroStats(2), AdrenalineShotSpell(), "",
            "", HeroSprite.SERGEANT_SWAN,
            listOf(
                DelinquentDuck::class,
                GuardGoose::class,
                PrivatePenguin::class,
                DelinquentDuck::class,
                GuardGoose::class,
                PrivatePenguin::class,
            )
        ) {}
        assertEquals(2, hero.currentStats.actionPoints)
        assertEquals(2, hero.baseStats.actionPoints)
        hero.applyStatsModifier(HeroStatsModifier { it.copy(actionPoints = it.actionPoints - 1) })
        assertEquals("Stats modifier -1 AP was not applied", 1, hero.currentStats.actionPoints)
        assertEquals("Base stats were modified by modifier", 2, hero.baseStats.actionPoints)
        hero.applyStatsModifier(HeroStatsModifier { it.copy(actionPoints = it.actionPoints + 1) })
        assertEquals("Stats modifier +1 AP was not applied", 2, hero.currentStats.actionPoints)
        hero.performAction(ActionData.MoveUnit("", GridVector(0, 0), GridVector(0, 0), 2))
        assertEquals("APs were not reduced by 2 from action", 0, hero.currentStats.actionPoints)
        hero.nextTurn()
        assertEquals(
            "APs were not restored to base at next turn", 2,
            hero.currentStats.actionPoints
        )
    }

    @Test
    fun testHeroNotAllowedToPerformActionCostingMoreThanCurrentActionPoints() {
        val hero = object : Hero<AdrenalineShotSpell>(
            HeroStats(2), AdrenalineShotSpell(), "",
            "", HeroSprite.SERGEANT_SWAN,
            listOf(
                DelinquentDuck::class,
                GuardGoose::class,
                PrivatePenguin::class,
                DelinquentDuck::class,
                GuardGoose::class,
                PrivatePenguin::class,
            )
        ) {}
        assertEquals("Wrong initial number of APs", 2, hero.currentStats.actionPoints)
        assertThrows(
            "Hero::performAction failed to error when costing more than current action points",
            IllegalStateException::class.java
        ) {
            hero.performAction(ActionData.MoveUnit("", GridVector(0, 0), GridVector(0, 0), 3))
        }
        assertEquals(
            "Number of APs changed after disallowed action prevented", 2,
            hero.currentStats.actionPoints
        )
        hero.performAction(ActionData.MoveUnit("", GridVector(0, 0), GridVector(0, 0), 1))
        assertEquals(
            "Wrong number of APs after action", 1,
            hero.currentStats.actionPoints
        )
        assertThrows(
            "Hero::performAction failed to error when costing more than current action points",
            IllegalStateException::class.java
        ) {
            hero.performAction(ActionData.MoveUnit("", GridVector(0, 0), GridVector(0, 0), 2))
        }
        assertEquals(
            "Number of APs changed after disallowed action prevented", 1,
            hero.currentStats.actionPoints
        )
    }
}
