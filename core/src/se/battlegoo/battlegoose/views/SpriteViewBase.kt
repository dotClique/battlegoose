package se.battlegoo.battlegoose.views

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import se.battlegoo.battlegoose.datamodels.ScreenVector

abstract class SpriteViewBase(texturePath: String) : ViewBase() {

    private val texture = Texture(texturePath)
    private var textureRegion = TextureRegion(texture)
    protected val sprite = Sprite(textureRegion)

    var size: ScreenVector = ScreenVector(sprite.width, sprite.height)
        get() = ScreenVector(sprite.width, sprite.height)
        set(value) {
            val textureWidthHeightRatio = textureRegion.regionWidth / textureRegion.regionHeight
            field = if (value.x / value.y > textureWidthHeightRatio) {
                ScreenVector(value.y * textureWidthHeightRatio, value.y)
            } else {
                ScreenVector(value.x, value.x / textureWidthHeightRatio)
            }
            sprite.setSize(field.x, field.y)
        }

    var position: ScreenVector = ScreenVector(sprite.x, sprite.y)
        get() = ScreenVector(sprite.x, sprite.y)
        set(value) {
            sprite.setPosition(value.x, value.y)
            field = value
        }

    fun overlaps(other: SpriteViewBase): Boolean =
        this.sprite.boundingRectangle.overlaps(other.sprite.boundingRectangle)

    override fun render(sb: SpriteBatch) = sprite.draw(sb)

    override fun dispose() = texture.dispose()
}
