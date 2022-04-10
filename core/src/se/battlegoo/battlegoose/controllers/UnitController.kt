package se.battlegoo.battlegoose.controllers

import se.battlegoo.battlegoose.models.units.UnitModel
import se.battlegoo.battlegoose.views.UnitObserver
import se.battlegoo.battlegoose.views.UnitViewBase

abstract class UnitController(
    private val unitModel: UnitModel,
    private val unitView: UnitViewBase,
    private val onUnitClick: () -> Unit
) : // Unit which means Void which means ()
    ControllerBase(unitView), UnitObserver {

    abstract fun updateUnit(dt: Float)

    init {
        unitView.subscribe(this)
    }

    override fun update(dt: Float) {
        unitView.registerInput()
        updateUnit(dt)
    }

    fun showRangeData() = object {
        val range = unitModel.currentStats.range
        val speed = unitModel.currentStats.speed
        val isFlying = unitModel.currentStats.isFlying
    }

    override fun onClick() {
        onUnitClick() // should toggle some sort of external state.
    }
}
