package se.battlegoo.battlegoose.controllers

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.models.units.UnitModel
import se.battlegoo.battlegoose.views.UnitStatsView

abstract class UnitStatsController(
    unitModel: UnitModel,
    private val statsView: UnitStatsView
) : ControllerBase(statsView) {

    private var showView: Boolean = false
    override fun update(dt: Float) {
    }
    init {
        statsView.unit = unitModel
    }

    fun setUnit(unit: UnitModel) {
        statsView.unit = unit
        showView = true
    }

    fun disableView() {
        showView = false
    }

    override fun render(sb: SpriteBatch) {
        if (showView) super.render(sb)
    }
}
