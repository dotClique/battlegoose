package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import se.battlegoo.battlegoose.Game

abstract class GameState {
    init {
        initialize()
    }

    fun initialize() {
        Game.stage = Stage(Game.viewPort, Game.batch)
        Gdx.input.inputProcessor = Game.stage
    }

    abstract fun update(dt: Float)
    abstract fun render(sb: SpriteBatch)
    abstract fun dispose()
}
