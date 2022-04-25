package se.battlegoo.battlegoose.views.utils

import com.badlogic.gdx.graphics.g2d.SpriteBatch

abstract class ViewBase {
    open fun registerInput() {}
    abstract fun render(sb: SpriteBatch)
    abstract fun dispose()
}
