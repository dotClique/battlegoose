package se.battlegoo.battlegoose.views.heroes

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.datamodels.ScreenVector
import se.battlegoo.battlegoose.models.heroes.HeroSprite
import se.battlegoo.battlegoose.utils.fitScale
import se.battlegoo.battlegoose.views.utils.ViewBase

class HeroIconView(
    position: ScreenVector,
    maxSize: ScreenVector,
    heroSprite: HeroSprite
) : ViewBase() {

    companion object {
        // Relative values in percentage (0f - 1f) to place elements atop the background
        private const val HERO_MARGIN_DOWN = 0.05f
        private const val HERO_MARGIN_LEFT = 0.05f
        private const val HERO_WIDTH = 0.9f
        private const val HERO_HEIGHT = 0.9f

        private const val MY_TURN_ICON_WIDTH_HEIGHT = 0.4f
    }

    private val myTurnIconWidth = maxSize.x * MY_TURN_ICON_WIDTH_HEIGHT
    private val myTurnIconHeight = maxSize.y * MY_TURN_ICON_WIDTH_HEIGHT

    var showMyTurn: Boolean = false

    private val showMyTurnTexture: Texture = Texture(
        "heroes/heroIcon/myTurnIcon.png"
    )

    private val backgroundTexture: Texture = Texture(
        "heroes/heroIcon/heroIconBackground.png"
    )
    private val borderTexture: Texture = Texture(
        "heroes/heroIcon/heroIconBorder.png"
    )
    private val heroTexture: Texture = Texture(
        when (heroSprite) {
            HeroSprite.SERGEANT_SWAN -> "heroes/sergeantSwan.png"
            HeroSprite.MAJOR_MALLARD -> "heroes/majorMallard.png"
            HeroSprite.ADMIRAL_ALBATROSS -> "heroes/admiralAlbatross.png"
        }
    )

    private val backgroundSprite = Sprite(backgroundTexture)
    private val heroSprite = Sprite(heroTexture)
    private val borderSprite = Sprite(borderTexture)
    private val showMyTurnSprite = Sprite(showMyTurnTexture)

    init {

        // define relative sizes and offsets
        val (containerSize, containerOffset) = fitScale(backgroundTexture, maxSize)
        // background and border have the same aspect ratio
        val heroBaseline = ScreenVector(
            position.x + containerOffset.x + containerSize.x * HERO_MARGIN_LEFT,
            position.y + containerOffset.y + containerSize.y * HERO_MARGIN_DOWN
        )
        val heroImage = ScreenVector(
            containerSize.x * HERO_WIDTH,
            containerSize.y * HERO_HEIGHT
        )
        val(heroSize, heroOffset) = fitScale(heroTexture, heroImage)

        // Set positions and sizes with defined values
        backgroundSprite.setPosition(
            position.x,
            position.y
        )
        backgroundSprite.setSize(
            containerSize.x,
            containerSize.y
        )
        borderSprite.setPosition(
            position.x,
            position.y
        )
        borderSprite.setSize(
            containerSize.x,
            containerSize.y
        )

        this.heroSprite.setPosition(
            heroBaseline.x + heroOffset.x,
            heroBaseline.y + heroOffset.y
        )
        this.heroSprite.setSize(heroSize.x, heroSize.y)

        showMyTurnSprite.setPosition(
            position.x - myTurnIconWidth / 4,
            position.y - myTurnIconWidth / 4
        )
        showMyTurnSprite.setSize(
            myTurnIconWidth,
            myTurnIconHeight
        )

        backgroundSprite.color = Color.WHITE
    }

    override fun render(sb: SpriteBatch) {
        backgroundSprite.draw(sb)
        heroSprite.draw(sb)
        borderSprite.draw(sb)
        if (showMyTurn) {
            showMyTurnSprite.draw(sb)
        }
    }

    override fun dispose() {
        backgroundTexture.dispose()
        heroTexture.dispose()
        borderTexture.dispose()
        showMyTurnTexture.dispose()
    }
}
