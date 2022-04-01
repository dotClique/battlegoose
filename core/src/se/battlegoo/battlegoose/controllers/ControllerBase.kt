package se.battlegoo.battlegoose.controllers

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.views.ViewBase

abstract class ControllerBase(private val view: ViewBase) {
    abstract fun update(dt: Float)
    open fun render(sb: SpriteBatch) {
        view.render(sb)
    }

    open fun dispose() {
        view.dispose()
    }
}
