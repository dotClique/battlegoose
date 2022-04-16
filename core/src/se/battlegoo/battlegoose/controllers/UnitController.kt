package se.battlegoo.battlegoose.controllers

import se.battlegoo.battlegoose.ScreenVector
import se.battlegoo.battlegoose.models.units.UnitModel
import se.battlegoo.battlegoose.views.UnitView

open class UnitController(
    val unitModel: UnitModel,
    private val unitView: UnitView
) :
    ControllerBase(unitView) {

    var viewSize: ScreenVector by unitView::size
    var viewPosition: ScreenVector by unitView::position
    var selected: Boolean by unitView::focused

    override fun update(dt: Float) {}

    fun showRangeData() = object {
        val range = unitModel.currentStats.range
        val speed = unitModel.currentStats.speed
        val isFlying = unitModel.currentStats.isFlying
    }
}
