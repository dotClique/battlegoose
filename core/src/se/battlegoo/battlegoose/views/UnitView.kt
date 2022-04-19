package se.battlegoo.battlegoose.views

class UnitView(
    unitSprite: UnitSprite,
    textureStartFacingDirection: FacingDirection
) : SpriteViewBase(
    when (unitSprite) {
        UnitSprite.PRIVATE_PENGUIN -> "penguin.png"
        UnitSprite.SPITFIRE_SEAGULL -> "goose.png"
    }
) {

    private var facingDirection = textureStartFacingDirection
        set(value) {
            if (value == this.facingDirection) return
            sprite.flip(true, false)
            field = value
        }

    var focused: Boolean = false

    init {
        sprite.flip(facingDirection == FacingDirection.LEFT, false)
    }
}
