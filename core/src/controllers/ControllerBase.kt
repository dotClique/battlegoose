package controllers

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import views.ViewBase

abstract class ControllerBase(private val model: Any, private val view: ViewBase) {
    abstract fun update(dt: Double)
    open fun render(sb: SpriteBatch) {
        view.render(sb)
    }
    open fun dispose() {
        view.dispose()
    }
}