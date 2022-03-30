package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

class ButtonView(
    val cam: OrthographicCamera,
    texturePath: String,
    private val xPos: Float,
    private val yPos: Float,
    scale: Float = 1f
) : ViewBase() {

    private val buttonTexture = Texture(texturePath)
    private val buttonTextureRegion = TextureRegion(buttonTexture)

    private val button = Rectangle(
        xPos,
        yPos,
        buttonTextureRegion.regionWidth * scale,
        buttonTextureRegion.regionHeight * scale
    )

    val btnWidth: Float = buttonTextureRegion.regionWidth * scale
    val btnHeight: Float = buttonTextureRegion.regionHeight * scale

    /**
     * Handle whether a button is pressed or not
     *
     * @return boolean
     */
    fun isPressed(): Boolean {
        // Invert y because of different axis directions of cam and gdx
        val yInverted = Gdx.graphics.height - Gdx.input.y.toFloat()

        val tmp = Vector2(Gdx.input.x.toFloat(), yInverted)
        val position = gdxToCam(tmp.x, tmp.y)

        return button.contains(position)
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
