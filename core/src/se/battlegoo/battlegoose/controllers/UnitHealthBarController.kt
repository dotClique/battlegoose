package se.battlegoo.battlegoose.controllers

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.models.units.UnitModel
import se.battlegoo.battlegoose.views.UnitHealthBarView

// Implement into UnitController?
class UnitHealthBarController(
    private val unitModel: UnitModel,
    private val healthBarView: UnitHealthBarView
) : ControllerBase(healthBarView) {

    private var showView: Boolean = false

    override fun update(dt: Float) {
        showView = (unitModel.currentStats.health < unitModel.currentStats.maxHealth)
        healthBarView.targetProgressValue =
            unitModel.currentStats.health.toFloat() / unitModel.currentStats.maxHealth.toFloat()
        healthBarView.currentHealth = unitModel.currentStats.health
        healthBarView.maxHealth = unitModel.currentStats.maxHealth
    }

    override fun render(sb: SpriteBatch) {
        if (showView) super.render(sb)
    }
}
