package se.battlegoo.battlegoose

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.battlegoo.battlegoose.models.Battle
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.models.heroes.HeroStats
import se.battlegoo.battlegoose.models.heroes.SeargentSwan
import se.battlegoo.battlegoose.models.spells.ActiveSpell
import se.battlegoo.battlegoose.models.spells.AdrenalinBoostSpell
import se.battlegoo.battlegoose.models.spells.Spell

class SpellTest {
    @Test
    fun testAdrenalinBoostSpell() {
        val hero = object : Hero(HeroStats(1), AdrenalinBoostSpell(), "", "") {}
        val battle = Battle(hero)
        val spell = hero.spell.cast()
        assertTrue(
            "ActiveSpell saved parent spell instance incorrect",
            spell.baseSpell is AdrenalinBoostSpell
        )
        assertEquals(
            "Wrong inital number of action points",
            1,
            hero.currentStats.actionPoints
        )
        for (i in 1..spell.duration) {
            spell.apply(battle)
            assertEquals(
                "Wrong number of action points after $i applications", 2,
                hero.currentStats.actionPoints
            )
            hero.nextTurn()
        }
        assertTrue("Spell not finished after 3 turns", spell.finished)
        for (i in 1..4) {
            spell.apply(battle)
            assertEquals(
                "Number of action points changed even after spell finished", 1,
                hero.currentStats.actionPoints
            )
            assertTrue("Spell stopped being finished", spell.finished)
            hero.nextTurn()
        }
    }

    @Test
    fun testActiveSpellCallsImplementationCorrectNumberOfTimes() {
        val battle = Battle(SeargentSwan())
        var counter = 0
        val spell = object : Spell("test", "t2") {
            override fun cast(): ActiveSpell {
                return object : ActiveSpell(5, this) {
                    override fun applyImplementation(battle: Battle, turnsSinceCast: Int) {
                        counter += 1
                    }
                }
            }
        }
        val activeSpell = spell.cast()
        assertEquals("Spell::cast called apply by itself", 0, counter)
        for (i in 1..5) {
            activeSpell.apply(battle)
            assertEquals("applyImplementation was not called $i times by apply", i, counter)
        }
        assertTrue(
            "Spell was not finished after being called number of times equal its duration",
            activeSpell.finished
        )
    }
}
