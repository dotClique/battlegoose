package se.battlegoo.battlegoose.views.units

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.utils.TextureAsset
import se.battlegoo.battlegoose.views.utils.Shaders
import se.battlegoo.battlegoose.views.utils.SpriteViewBase

class UnitView(
    unitSprite: UnitSprite,
    val textureStartFacingDirection: FacingDirection,
    val ownedByThisPlayer: Boolean = true
) : SpriteViewBase(
    when (unitSprite) {
        UnitSprite.PRIVATE_PENGUIN -> TextureAsset.UNIT_PRIVATE_PENGUIN
        UnitSprite.SPITFIRE_SEAGULL -> TextureAsset.UNIT_SPITFIRE_SEAGULL
        UnitSprite.DELINQUENT_DUCK -> TextureAsset.UNIT_DELINQUENT_DUCK
        UnitSprite.GUARD_GOOSE -> TextureAsset.UNIT_GUARD_GOOSE
    }
) {

    private var facingDirection = textureStartFacingDirection
        set(value) {
            if (value == this.facingDirection) return
            sprite.flip(true, false)
            field = value
        }

    var converted: Boolean = false
        set(value) {
            if (value == field) return
            field = value
            facingDirection = if (value)
                textureStartFacingDirection.flipped()
            else
                textureStartFacingDirection
        }

    override fun render(sb: SpriteBatch) {
        val previousShader = sb.shader
        if (converted) {
            sb.shader = if (ownedByThisPlayer) Shaders.RedShift else Shaders.GreenShift
        }
        super.render(sb)
        sb.shader = previousShader
    }

    var focused: Boolean = false

    init {
        sprite.flip(facingDirection == FacingDirection.LEFT, false)
    }
}

enum class FacingDirection {
    RIGHT,
    LEFT;

    fun flipped(): FacingDirection = when (this) {
        RIGHT -> LEFT
        LEFT -> RIGHT
    }
}
