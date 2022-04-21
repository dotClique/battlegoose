package se.battlegoo.battlegoose

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.battlegoo.battlegoose.models.Battle
import se.battlegoo.battlegoose.models.BattleMap
import se.battlegoo.battlegoose.models.BattleMapBackground
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.models.heroes.HeroSprite
import se.battlegoo.battlegoose.models.heroes.HeroStats
import se.battlegoo.battlegoose.models.heroes.SergeantSwan
import se.battlegoo.battlegoose.models.spells.ActiveSpell
import se.battlegoo.battlegoose.models.spells.AdrenalineShotSpell
import se.battlegoo.battlegoose.models.spells.Bird52ActiveSpell
import se.battlegoo.battlegoose.models.spells.Bird52Spell
import se.battlegoo.battlegoose.models.spells.EphemeralAllegianceSpell
import se.battlegoo.battlegoose.models.spells.Spell
import se.battlegoo.battlegoose.models.units.DelinquentDuck
import se.battlegoo.battlegoose.models.units.GuardGoose
import se.battlegoo.battlegoose.models.units.PrivatePenguin
import se.battlegoo.battlegoose.models.units.SpitfireSeagull

class SpellTest {
    @Test
    fun testAdrenalineShotSpell() {
        val hero = object : Hero(
            HeroStats(1), AdrenalineShotSpell(), "",
            "", HeroSprite.SERGEANT_SWAN
        ) {}
        val battle = Battle(
            hero,
            SergeantSwan(),
            BattleMap(BattleMapBackground.SAND, GridVector(10, 6))
        )
        val spell = hero.spell.cast()
        assertTrue(
            "ActiveSpell saved parent spell instance incorrect",
            spell.baseSpell is AdrenalineShotSpell
        )
        assertEquals(
            "Wrong inital number of action points",
            1,
            hero.currentStats.actionPoints
        )
        for (i in 1..spell.baseSpell.duration) {
            spell.apply(battle)
            assertEquals(
                "Wrong number of action points after $i applications", 2,
                hero.currentStats.actionPoints
            )
            hero.nextTurn()
        }
        assertTrue("Spell not finished after ${spell.baseSpell.duration} turns", spell.finished)
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
    fun testEphemeralAllegianceSpell() {
        val hero1 = object : Hero(
            HeroStats(1), EphemeralAllegianceSpell(), "", "", HeroSprite.SERGEANT_SWAN
        ) {}
        val hero2 = SergeantSwan()
        val battle = Battle(
            hero1, hero2, BattleMap(BattleMapBackground.SAND, GridVector(10, 6))
        )

        battle.battleMap.placeUnit(GuardGoose(hero1), GridVector(1, 1))
        battle.battleMap.placeUnit(PrivatePenguin(hero1), GridVector(1, 2))
        battle.battleMap.placeUnit(SpitfireSeagull(hero2), GridVector(2, 1))
        battle.battleMap.placeUnit(DelinquentDuck(hero2), GridVector(2, 2))

        assertEquals(
            "Hero 1 should have exactly 2 units with allegiance to them",
            2,
            battle.battleMap.count { battle.battleMap.getUnit(it)?.allegiance == hero1 }
        )
        assertEquals(
            "Hero 2 should have exactly 2 units with allegiance to them",
            2,
            battle.battleMap.count { battle.battleMap.getUnit(it)?.allegiance == hero2 }
        )
        assertEquals(
            "Hero 1 should have ownership of exactly 2 units",
            2,
            battle.battleMap.count { battle.battleMap.getUnit(it)?.owner == hero1 }
        )
        assertEquals(
            "Hero 2 should have ownership of exactly 2 units",
            2,
            battle.battleMap.count { battle.battleMap.getUnit(it)?.owner == hero2 }
        )

        val spell = hero1.spell.cast()
        assertTrue(
            "ActiveSpell save parent spell instance incorrectly",
            spell.baseSpell is EphemeralAllegianceSpell
        )

        for (i in 1 until spell.baseSpell.duration - 1) {
            spell.apply(battle)
            assertEquals(
                "Hero 1 should have 3 units with allegiance to them",
                3,
                battle.battleMap.count { battle.battleMap.getUnit(it)?.allegiance == hero1 }
            )
            assertEquals(
                "Hero 2 should have 1 unit with allegiance to them",
                1,
                battle.battleMap.count { battle.battleMap.getUnit(it)?.allegiance == hero2 }
            )
            assertEquals(
                "Hero 1 should maintain ownership of only 2 units",
                2,
                battle.battleMap.count { battle.battleMap.getUnit(it)?.owner == hero1 }
            )
            assertEquals(
                "Hero 2 should maintain ownership of 2 units",
                2,
                battle.battleMap.count { battle.battleMap.getUnit(it)?.owner == hero2 }
            )
        }
        assertTrue(
            "Spell finished too early, after only ${spell.baseSpell.duration - 2} turns",
            !spell.finished
        )
        spell.apply(battle)
        assertTrue(
            "Spell finished too early, after only ${spell.baseSpell.duration - 1} turns",
            !spell.finished
        )
        spell.apply(battle)
        assertTrue("Spell not finished after ${spell.baseSpell.duration} turns", spell.finished)
        for (i in 1 until 5) {
            spell.apply(battle)

            assertEquals(
                "Hero 1 should have exactly 2 units with allegiance to them",
                2,
                battle.battleMap.count { battle.battleMap.getUnit(it)?.allegiance == hero1 }
            )
            assertEquals(
                "Hero 2 should have exactly 2 units with allegiance to them",
                2,
                battle.battleMap.count { battle.battleMap.getUnit(it)?.allegiance == hero2 }
            )
            assertEquals(
                "Hero 1 should have ownership of exactly 2 units",
                2,
                battle.battleMap.count { battle.battleMap.getUnit(it)?.owner == hero1 }
            )
            assertEquals(
                "Hero 2 should have ownership of exactly 2 units",
                2,
                battle.battleMap.count { battle.battleMap.getUnit(it)?.owner == hero2 }
            )
            assertTrue("Spell stopped being finished", spell.finished)
        }
    }

    @Test
    fun testBird52Spell() {
        val hero = object : Hero(
            HeroStats(1), Bird52Spell(), "",
            "", HeroSprite.SERGEANT_SWAN
        ) {}
        val battle = Battle(
            hero,
            SergeantSwan(),
            BattleMap(BattleMapBackground.SAND, GridVector(10, 6))
        )

        val unit1Team1 = GuardGoose(battle.hero1)
        val unit2Team1 = PrivatePenguin(battle.hero1)
        val unit1Team2 = SpitfireSeagull(battle.hero2)
        val unit2Team2 = DelinquentDuck(battle.hero2)

        battle.battleMap.placeUnit(unit1Team1, GridVector(1, 1))
        battle.battleMap.placeUnit(unit2Team1, GridVector(5, 1))
        battle.battleMap.placeUnit(unit1Team2, GridVector(2, 2))
        battle.battleMap.placeUnit(unit2Team2, GridVector(4, 2))

        val spell = hero.spell.cast() as Bird52ActiveSpell
        assertTrue(
            "ActiveSpell saved parent spell instance incorrect",
            spell.baseSpell is Bird52Spell
        )

        var stats1Team1 = unit1Team1.currentStats
        var stats2Team1 = unit2Team1.currentStats
        var stats1Team2 = unit1Team2.currentStats
        var stats2Team2 = unit2Team2.currentStats

        spell.apply(battle)

        assertEquals(
            "${unit1Team1.name} is outside range and should not take damage",
            stats1Team1.health, unit1Team1.currentStats.health
        )
        assertEquals(
            "${unit1Team2.name} is outside range and should not take damage",
            stats1Team2.health, unit1Team2.currentStats.health
        )

        assertEquals(
            "${unit2Team1.name} should have taken ${spell.attackDamage} damage",
            stats2Team1.health - spell.attackDamage, unit2Team1.currentStats.health
        )
        assertEquals(
            "${unit2Team2.name} should have taken ${spell.attackDamage} damage",
            stats2Team2.health - spell.attackDamage, unit2Team2.currentStats.health
        )

        assertTrue("Spell not finished after ${spell.baseSpell.duration} turns", spell.finished)

        for (i in 1..4) {
            stats1Team1 = unit1Team1.currentStats
            stats2Team1 = unit2Team1.currentStats
            stats1Team2 = unit1Team2.currentStats
            stats2Team2 = unit2Team2.currentStats

            spell.apply(battle)

            assertEquals(
                "${unit1Team1.name} took damage after spell was finished",
                stats1Team1.health, unit1Team1.currentStats.health
            )
            assertEquals(
                "${unit1Team2.name} took damage after spell was finished",
                stats1Team2.health, unit1Team2.currentStats.health
            )
            assertEquals(
                "${unit2Team1.name} took damage after spell was finished",
                stats2Team1.health, unit2Team1.currentStats.health
            )
            assertEquals(
                "${unit2Team2.name} took damage after spell was finished",
                stats2Team2.health, unit2Team2.currentStats.health
            )

            assertTrue("Spell stopped being finished", spell.finished)
        }
    }

    @Test
    fun testActiveSpellCallsImplementationCorrectNumberOfTimes() {
        val battle = Battle(
            SergeantSwan(),
            SergeantSwan(),
            BattleMap(BattleMapBackground.SAND, GridVector(10, 6))
        )
        var counter = 0
        val testDuration = 5
        val spell = object : Spell("test", "t2", testDuration, 2) {
            override fun cast(): ActiveSpell {
                return object : ActiveSpell(this) {
                    override fun applyImplementation(battle: Battle, turnsSinceCast: Int) {
                        counter += 1
                    }
                }
            }
        }
        val activeSpell = spell.cast()
        assertEquals("Spell::cast called apply by itself", 0, counter)
        for (i in 1..testDuration) {
            activeSpell.apply(battle)
            assertEquals("applyImplementation was not called $i times by apply", i, counter)
        }
        assertTrue(
            "Spell was not finished after being called number of times equal its duration",
            activeSpell.finished
        )
    }
}
