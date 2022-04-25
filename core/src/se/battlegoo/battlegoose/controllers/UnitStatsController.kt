package se.battlegoo.battlegoose.controllers

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.models.units.UnitModel
import se.battlegoo.battlegoose.views.units.UnitStatsView

class UnitStatsController(
    unitModel: UnitModel?,
    private val statsView: UnitStatsView
) : ControllerBase(statsView) {

    var showView: Boolean = false
    var unit: UnitModel? by statsView::unit

    init {
        unit = unitModel
    }

    override fun update(dt: Float) {}

    override fun render(sb: SpriteBatch) {
        if (showView) super.render(sb)
    }
}
