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

open class Unit (unitStats: UnitStats, modifier: UnitStatsModifier) : IObservable{
    //private var baseUnitStats: UnitStats = unitStats;
    private var transformedUnitStats: UnitStats = unitStats;
    private val modifierList :ArrayList<UnitStatsModifier> = ArrayList<UnitStatsModifier>()


    override val observers: ArrayList<IObserver>
        get() = TODO("Not yet implemented")

    fun addModifier(modifier: UnitStatsModifier) {
        modifierList.add(modifier);
        transformedUnitStats = modifier.modify(transformedUnitStats);
        checkStatState()
    }

    public fun takeDamage(damage: Int) {
        //using flat reduction for now
        transformedUnitStats.flatReduceIncomingDamage(damage)
        checkStatState()
    }

    private fun handleDeath() {
        sendUpdateEvent("Dead");
    }

    private fun checkStatState() {
        if (transformedUnitStats.health <= 0) {
            handleDeath()
        }
    }




}