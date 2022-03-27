package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import kotlin.properties.Delegates


class ButtonView(val cam: OrthographicCamera, texturePath: String,
                 private val xPos: Float,
                 private val yPos: Float,
                 private val scale: Float = 1f
): ViewBase() {

    private val buttonTexture = Texture(texturePath)
    private val buttonTextureRegion = TextureRegion(buttonTexture)
    private val button = Rectangle(
        buttonTextureRegion.regionX.toFloat(),
        buttonTextureRegion.regionY.toFloat(),
        buttonTextureRegion.regionWidth.toFloat(),
        buttonTextureRegion.regionHeight.toFloat()
    )

    val btnWidth: Float = buttonTextureRegion.regionWidth*scale
    val btnHeight: Float = buttonTextureRegion.regionHeight*scale

    /**
     * Handle whether a button is pressed or not
     *
     * @return boolean
     */
    fun isPressed(): Boolean {
        return if (Gdx.input.justTouched()) {
            val yInverted = Gdx.graphics.height - Gdx.input.y.toFloat() // Because of different axis directions

            val tmp = Vector2(Gdx.input.x.toFloat(), yInverted)
            val position = gdxToCam(tmp.x, tmp.y)

            button.contains(position)
        } else false
    }

    override fun render(sb: SpriteBatch) {
        sb.draw(buttonTextureRegion, xPos, yPos, this.btnWidth, this.btnHeight)
    }

    override fun dispose() {
        buttonTexture.dispose()
    }

    /**
     * Public function that scales Gdx values to Viewport values
     */
    fun gdxToCam(x: Float, y: Float): Vector2 {
        return Vector2(
            x / Gdx.graphics.width.toFloat() * cam.viewportWidth,
            y / Gdx.graphics.height.toFloat() * cam.viewportHeight
        )
    }
}
