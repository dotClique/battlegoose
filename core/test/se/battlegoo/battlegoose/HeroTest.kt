package se.battlegoo.battlegoose

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import se.battlegoo.battlegoose.models.Action
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.models.heroes.HeroSprite
import se.battlegoo.battlegoose.models.heroes.HeroStats
import se.battlegoo.battlegoose.models.heroes.HeroStatsModifier
import se.battlegoo.battlegoose.models.spells.AdrenalineShotSpell

class HeroTest {
    @Test
    fun testHero() {
        val hero = object : Hero(
            HeroStats(2), AdrenalineShotSpell(), "",
            "", HeroSprite.SERGEANT_SWAN
        ) {}
        assertEquals(2, hero.currentStats.actionPoints)
        assertEquals(2, hero.baseStats.actionPoints)
        hero.applyStatsModifier(HeroStatsModifier { it.copy(actionPoints = it.actionPoints - 1) })
        assertEquals("Stats modifier -1 AP was not applied", 1, hero.currentStats.actionPoints)
        assertEquals("Base stats were modified by modifier", 2, hero.baseStats.actionPoints)
        hero.applyStatsModifier(HeroStatsModifier { it.copy(actionPoints = it.actionPoints + 1) })
        assertEquals("Stats modifier +1 AP was not applied", 2, hero.currentStats.actionPoints)
        hero.performAction(object : Action(2) {})
        assertEquals("APs were not reduced by 2 from action", 0, hero.currentStats.actionPoints)
        hero.nextTurn()
        assertEquals(
            "APs were not restored to base at next turn", 2,
            hero.currentStats.actionPoints
        )
    }

    @Test
    fun testHeroNotAllowedToPerformActionCostingMoreThanCurrentActionPoints() {
        val hero = object : Hero(
            HeroStats(2), AdrenalineShotSpell(), "",
            "", HeroSprite.SERGEANT_SWAN
        ) {}
        assertEquals("Wrong initial number of APs", 2, hero.currentStats.actionPoints)
        assertThrows(
            "Hero::performAction failed to error when costing more than current action points",
            AssertionError::class.java
        ) {
            hero.performAction(object : Action(3) {})
        }
        assertEquals(
            "Number of APs changed after disallowed action prevented", 2,
            hero.currentStats.actionPoints
        )
        hero.performAction(object : Action(1) {})
        assertEquals(
            "Wrong number of APs after action", 1,
            hero.currentStats.actionPoints
        )
        assertThrows(
            "Hero::performAction failed to error when costing more than current action points",
            AssertionError::class.java
        ) {
            hero.performAction(object : Action(2) {})
        }
        assertEquals(
            "Number of APs changed after disallowed action prevented", 1,
            hero.currentStats.actionPoints
        )
    }
}
