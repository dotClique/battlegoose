package se.battlegoo.battlegoose

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.ScreenUtils
import se.battlegoo.battlegoose.gamestates.GameStateManager
import se.battlegoo.battlegoose.gamestates.MainMenuState

class Game : ApplicationAdapter() {
    private lateinit var batch: SpriteBatch

    companion object {
        const val WIDTH = 2280
        const val HEIGHT = 1080
        const val TITLE = "Battlegoose"
    }

    override fun create() {
        batch = SpriteBatch()
        GameStateManager.push(MainMenuState())
    }

    override fun render() {
        ScreenUtils.clear(1f, 1f, 1f, 1f)
        GameStateManager.update(Gdx.graphics.deltaTime)
        GameStateManager.render(batch)
    }

    override fun dispose() {
        batch.dispose()
    }
}
