package views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2

abstract class ViewBase(
    texturePath: String,
    protected val textureStartFacingDirection: FacingDirection = FacingDirection.RIGHT
) {

    protected val texture = Texture(texturePath)
    protected var sprite = Sprite(texture)
    protected var facingDirection = textureStartFacingDirection

    fun clicked(): Boolean {
        if (!Gdx.input.justTouched()) return false
        val touchPoint = Vector2(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
        val thisRect = sprite.boundingRectangle
        if (thisRect.contains(touchPoint)) return true
        return false
    }

    fun overlaps(other: ViewBase): Boolean {
        val thisRect = this.sprite.boundingRectangle
        val otherRect = other.sprite.boundingRectangle
        return thisRect.overlaps(otherRect)
    }

    fun setDirection(newDirection: FacingDirection) {
        if (newDirection == this.facingDirection) return;
        facingDirection = newDirection
        sprite.flip(true, false)
    }

    fun render(sb: SpriteBatch) {
        sprite.draw(sb)
    }

    fun dispose() {
        texture.dispose()
    }

}