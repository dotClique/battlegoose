package se.battlegoo.battlegoose.views

class UnitView(
    unitSprite: UnitSprite,
    textureStartFacingDirection: FacingDirection
) : SpriteViewBase(
    when (unitSprite) {
        UnitSprite.PRIVATE_PENGUIN -> "units/penguin.png"
        UnitSprite.SPITFIRE_SEAGULL -> "units/seagull.png"
        UnitSprite.DELINQUENT_DUCK -> "units/duck.png"
        UnitSprite.GUARD_GOOSE -> "units/guardGoose.png"
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
