package com.progark.battlegoose.models.units

import com.badlogic.gdx.utils.Null

open class UnitStatsModifier {

    open val descriptor : String = "Standard modifier"

    open fun modify(unitStats: UnitStats) : UnitStats {
        return unitStats;
    }

    open fun unmodify(unitStats: UnitStats) : UnitStats {
        return unitStats
    }
}