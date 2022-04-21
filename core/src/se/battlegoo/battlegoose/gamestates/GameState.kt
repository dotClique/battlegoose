package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import se.battlegoo.battlegoose.Game

abstract class GameState {
    val stage = Stage(Game.viewPort, Game.batch)

    abstract fun update(dt: Float)
    abstract fun render(sb: SpriteBatch)

    fun disposeState() {
        stage.dispose()
        dispose()
    }

    protected abstract fun dispose()
}
