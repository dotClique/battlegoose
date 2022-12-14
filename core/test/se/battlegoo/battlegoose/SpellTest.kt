package se.battlegoo.battlegoose

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.battlegoo.battlegoose.datamodels.GridVector
import se.battlegoo.battlegoose.datamodels.SpellData
import se.battlegoo.battlegoose.models.Battle
import se.battlegoo.battlegoose.models.BattleMap
import se.battlegoo.battlegoose.models.BattleMapBackground
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.models.heroes.HeroSprite
import se.battlegoo.battlegoose.models.heroes.HeroStats
import se.battlegoo.battlegoose.models.heroes.SergeantSwan
import se.battlegoo.battlegoose.models.spells.ActiveSpell
import se.battlegoo.battlegoose.models.spells.AdrenalineShotSpell
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
        val battle = Battle(
            hero,
            SergeantSwan(),
            BattleMap(BattleMapBackground.DUNES, GridVector(10, 6)),
            "", true
        )
        assertTrue("Hero spell is wrong type", hero.spell is AdrenalineShotSpell)
        val spell = (hero.spell as AdrenalineShotSpell).cast(hero, SpellData.AdrenalineShot)
        assertEquals(
            "Wrong inital number of action points",
            1,
            hero.currentStats.actionPoints
        )
        spell.apply(battle)
        assertEquals(
            "Should have no effect on first application", 1,
            hero.currentStats.actionPoints
        )
        for (i in 2..spell.baseSpell.duration) {
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
            HeroStats(1), EphemeralAllegianceSpell(), "", "",
            HeroSprite.SERGEANT_SWAN,
            listOf(
                DelinquentDuck::class,
                GuardGoose::class,
                PrivatePenguin::class,
                DelinquentDuck::class,
                GuardGoose::class,
                PrivatePenguin::class,
            )
        ) {}
        val hero2 = SergeantSwan()
        val battle = Battle(
            hero1, hero2, BattleMap(BattleMapBackground.DUNES, GridVector(10, 6)), "", true
        )

        battle.battleMap.placeUnit(GuardGoose(hero1), GridVector(1, 1))
        battle.battleMap.placeUnit(PrivatePenguin(hero1), GridVector(1, 2))
        battle.battleMap.placeUnit(SpitfireSeagull(hero2), GridVector(2, 1))
        val targetUnit = DelinquentDuck(hero2)
        battle.battleMap.placeUnit(targetUnit, GridVector(2, 2))

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

        assertTrue("Wrong hero spell type", hero1.spell is EphemeralAllegianceSpell)
        val spell = (hero1.spell as EphemeralAllegianceSpell).cast(
            hero1,
            SpellData.EphemeralAllegiance(GridVector(2, 2))
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
            assertTrue(
                "Hero 1 should have ownership of the target unit",
                targetUnit.allegiance == hero1
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
        val battle = Battle(
            hero,
            SergeantSwan(),
            BattleMap(BattleMapBackground.DUNES, GridVector(10, 6)),
            "",
            true
        )

        val unit1Team1 = GuardGoose(battle.hero1)
        val unit2Team1 = PrivatePenguin(battle.hero1)
        val unit1Team2 = SpitfireSeagull(battle.hero2)
        val unit2Team2 = DelinquentDuck(battle.hero2)

        battle.battleMap.placeUnit(unit1Team1, GridVector(1, 1))
        battle.battleMap.placeUnit(unit2Team1, GridVector(5, 1))
        battle.battleMap.placeUnit(unit1Team2, GridVector(2, 2))
        battle.battleMap.placeUnit(unit2Team2, GridVector(4, 2))

        assertTrue("Wrong hero spell type", hero.spell is Bird52Spell)
        val spell = (hero.spell as Bird52Spell).cast(hero, SpellData.Bird52)

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
            "${unit2Team1.name} should have taken ${hero.spell.attackDamage} damage",
            stats2Team1.health - hero.spell.attackDamage, unit2Team1.currentStats.health
        )
        assertEquals(
            "${unit2Team2.name} should have taken ${hero.spell.attackDamage} damage",
            stats2Team2.health - hero.spell.attackDamage, unit2Team2.currentStats.health
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
        val hero1 = SergeantSwan()
        val battle = Battle(
            hero1,
            SergeantSwan(),
            BattleMap(BattleMapBackground.DUNES, GridVector(10, 6)),
            "",
            true
        )
        var counter = 0
        val testDuration = 5

        class TestSpell : Spell<SpellData.Bird52>("test", "t2", testDuration, 2) {
            override fun castImplementation(
                caster: Hero,
                data: SpellData.Bird52
            ): ActiveSpell<TestSpell> {
                return object : ActiveSpell<TestSpell>(this, hero1, data) {
                    override fun applyImplementation(battle: Battle, turnsSinceCast: Int) {
                        counter += 1
                    }
                }
            }
        }

        val spell = TestSpell()
        val activeSpell = spell.cast(hero1, SpellData.Bird52)
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
