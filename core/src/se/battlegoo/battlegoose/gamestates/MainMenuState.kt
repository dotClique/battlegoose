package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class MainMenuState :
    GameState() {

    private val background = Texture("heli1.png")
    private val playBtn = Texture("play.png")

    private fun handleInput() {
        if (Gdx.input.justTouched())
            GameStateManager.push(BattleState())
    }

    override fun update(dt: Float) {
        handleInput()
    }

    override fun render(sb: SpriteBatch) {
        val middleX = Gdx.graphics.width / 2f - playBtn.width / 2f
        val middleY = Gdx.graphics.height / 2f - playBtn.height / 2
        sb.begin()
        sb.draw(background, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        sb.draw(playBtn, middleX, middleY)
        sb.end()
    }

    override fun dispose() {
        background.dispose()
        playBtn.dispose()
    }
}
