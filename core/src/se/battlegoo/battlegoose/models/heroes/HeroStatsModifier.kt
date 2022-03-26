package se.battlegoo.battlegoose.models.heroes

data class HeroStatsModifier(val apply: (heroStats: HeroStats) -> HeroStats)
