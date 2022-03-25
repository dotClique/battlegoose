package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch

abstract class GameState {
    abstract fun update(dt: Float)
    abstract fun render(sb: SpriteBatch)
    abstract fun dispose()
}
