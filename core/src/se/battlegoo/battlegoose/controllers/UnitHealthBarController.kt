package se.battlegoo.battlegoose.controllers

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.datamodels.ScreenVector
import se.battlegoo.battlegoose.models.units.UnitModel
import se.battlegoo.battlegoose.views.units.UnitHealthBarView

class UnitHealthBarController(
    private val unitModel: UnitModel,
    private val healthBarView: UnitHealthBarView
) : ControllerBase(healthBarView) {

    private var showView: Boolean = false

    var viewWidth: Float by healthBarView::width
    var viewPosition: ScreenVector by healthBarView::position

    override fun update(dt: Float) {
        showView = (unitModel.currentStats.health < unitModel.currentStats.maxHealth)
        healthBarView.currentHealth = unitModel.currentStats.health
        healthBarView.maxHealth = unitModel.currentStats.maxHealth
    }

    override fun render(sb: SpriteBatch) {
        if (showView) super.render(sb)
    }
}
