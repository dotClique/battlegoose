package se.battlegoo.battlegoose.controllers

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.datamodels.ScreenVector
import se.battlegoo.battlegoose.models.units.UnitModel
import se.battlegoo.battlegoose.views.units.UnitHealthBarView
import se.battlegoo.battlegoose.views.units.UnitView

open class UnitController(
    val unitModel: UnitModel,
    private val unitView: UnitView
) : ControllerBase(unitView) {

    // Extra backing property to allow accessors with non-default implementations while delegating
    private var _viewSize: ScreenVector by unitView::size
    var viewSize: ScreenVector = _viewSize
        get() = _viewSize
        set(viewSize) {
            field = viewSize
            _viewSize = viewSize
            unitHealthBarController.viewWidth = healthBarWidth
        }

    private var _viewPosition: ScreenVector by unitView::position
    var viewPosition = _viewPosition
        get() = _viewPosition
        set(viewPosition) {
            field = viewPosition
            _viewPosition = viewPosition
            unitHealthBarController.viewPosition = healthBarPosition
            unitHealthBarController.viewWidth = healthBarWidth
        }

    var selected: Boolean by unitView::focused
    var converted: Boolean = false
        set(value) {
            field = value
            unitView.converted = converted
        }

    private var unitHealthBarController = UnitHealthBarController(
        unitModel,
        UnitHealthBarView(
            healthBarPosition,
            healthBarWidth
        )
    )

    private val healthBarWidth
        get() = viewSize.x

    private val healthBarPosition
        get() = ScreenVector(viewPosition.x, viewPosition.y + viewSize.y)

    override fun update(dt: Float) {
        unitHealthBarController.update(dt)
    }

    fun renderHealthBar(sb: SpriteBatch) {
        unitHealthBarController.render(sb)
    }
}
