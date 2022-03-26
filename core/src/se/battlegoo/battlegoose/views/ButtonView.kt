package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2

class ButtonView(texturePath: String): ViewBase() {

    private val buttonTexture = Texture(texturePath)
    private val buttonTextureRegion = TextureRegion(buttonTexture)
    private val button = Sprite(buttonTextureRegion)


    fun isPressed(): Boolean {
        return if (Gdx.input.justTouched()) {
            val position = Vector2(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
            val buttonBounds = button.boundingRectangle
            buttonBounds.contains(position)
        } else false
    }

    override fun render(sb: SpriteBatch) {
        sb.draw(buttonTextureRegion, 0f, 0f)
    }

    override fun dispose() {
        buttonTexture.dispose()
    }

    /**
     * Public function that scales Gdx values to Viewport values
     *
    fun gdxToCam(x: Float, y: Float): Vector2 {
        return Vector2(
            x / Gdx.graphics.width.toFloat() * cam.viewportWidth,
            y / Gdx.graphics.height.toFloat() * cam.viewportHeight
        )
    }
    **/
}
