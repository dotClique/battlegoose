package com.progark.battlegoose.models.units

interface IObserver {
    fun update(message: String)
}

interface IObservable {
    val observers: ArrayList<IObserver>

    public fun add(observer: IObserver) {
        observers.add(observer);
    }

    public fun remove(observer: IObserver) {
        observers.remove(observer);
    }

    open fun sendUpdateEvent(message: String) {
    observers.forEach { it.update(message) }
    }
}

open class Unit (unitStats: UnitStats, var name : String, var description : String) : IObservable{
    //private var baseUnitStats: UnitStats = unitStats;
    var transformedUnitStats: UnitStats = unitStats;
    val modifierList :ArrayList<UnitStatsModifier> = ArrayList<UnitStatsModifier>();


    override val observers: ArrayList<IObserver>
        get() = TODO("Not yet implemented")

    fun addModifier(modifier: UnitStatsModifier) {
        modifierList.add(modifier);
        transformedUnitStats = modifier.modify(transformedUnitStats);
        checkStatState();
    }

    fun silence() {
        for (i in modifierList.indices.reversed()) {
            transformedUnitStats = modifierList[i].unmodify(transformedUnitStats);
        }
        checkStatState();
    }

    public fun takeDamage(damage: Int) {
        //using flat reduction for now
        transformedUnitStats.linearReduceIncomingDamage(damage);
        checkStatState();
    }

    private fun handleDeath() {
        sendUpdateEvent("Dead");
    }

    open fun checkStatState() {
        if (transformedUnitStats.health <= 0) {
            handleDeath()
        }
    }




}