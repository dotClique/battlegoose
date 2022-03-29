package com.progark.battlegoose.models.units

class UnitStatsModifier (val modifyFunc: (UnitStats) -> UnitStats, val unmodifyFunc: (UnitStats) -> UnitStats, descriptor: String) {

    var descriptor : String = descriptor;

    open fun modify(unitStats: UnitStats) : UnitStats {
        return modifyFunc(unitStats);
    }

    open fun unmodify(unitStats: UnitStats) : UnitStats {
        return unmodifyFunc(unitStats);
    }
}