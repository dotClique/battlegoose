package gamestates

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class BattleState : GameState()
{
    private val texture = Texture("badlogic.jpg")
    override fun update(dt: Float) {
        if (Gdx.input.justTouched())
            GameStateManager.goBack()
    }

    override fun render(sb: SpriteBatch) {
        sb.begin()
        sb.draw(texture, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        sb.end()
    }

    override fun dispose() {
        texture.dispose()
    }
}