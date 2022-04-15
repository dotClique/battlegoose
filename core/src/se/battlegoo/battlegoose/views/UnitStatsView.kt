package se.battlegoo.battlegoose.views

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import se.battlegoo.battlegoose.models.units.UnitModel

class UnitStatsView(
    var unit: UnitModel,
    private val xPos: Float,
    private val yPos: Float,
    val width: Int
) : ViewBase() {

    private val texture = Texture("statsScroll.png")
    private val font = BitmapFont()

    val height =
        (texture.height.toFloat() / texture.width.toFloat() * width.toFloat()).toInt()

    private val scale =
        width.toFloat() / texture.width.toFloat()

    private val statsTextureRegion = TextureRegion(texture)

    private val xIndent = xPos + width / 4.5f

    private val statsSprite = Sprite(statsTextureRegion)

    init {
        font.data.setScale(scale * 3)
        font.setColor(0f, 0f, 0f, 9f)

        statsSprite.setCenter(xPos + width / 2, yPos + height / 2)
        statsSprite.setScale(scale)
    }

    override fun render(sb: SpriteBatch) {
        statsSprite.draw(sb)
        font.draw(
            sb,
            "[ ${unit.name} ]",
            xIndent,
            yPos + height * 13.5f / 16
        )
        font.draw(
            sb,
            "HP:\t${unit.currentStats.health}/${unit.currentStats.maxHealth}",
            xIndent,
            yPos + height * 12 / 16
        )
        font.draw(
            sb,
            "Attack:\t${unit.currentStats.attack}",
            xIndent,
            yPos + height * 11 / 16
        )
        font.draw(
            sb,
            "Defense:\t${unit.currentStats.defense}",
            xIndent,
            yPos + height * 10 / 16
        )
        font.draw(
            sb,
            "Speed:\t${unit.currentStats.speed}",
            xIndent,
            yPos + height * 9 / 16
        )
        font.draw(
            sb,
            "Range:\t${unit.currentStats.range}",
            xIndent,
            yPos + height * 8 / 16
        )
        if (unit.currentStats.isFlying) {
            font.draw(
                sb,
                "Flying",
                xIndent,
                yPos + height * 7 / 16
            )
        }
    }

    override fun dispose() {
        texture.dispose()
        font.dispose()
    }
}
