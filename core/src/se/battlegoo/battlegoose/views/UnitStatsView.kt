package se.battlegoo.battlegoose.views

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import se.battlegoo.battlegoose.ScreenVector
import se.battlegoo.battlegoose.models.units.UnitModel

class UnitStatsView(
    private val position: ScreenVector,
    val width: Int
) : ViewBase() {

    var unit: UnitModel? = null

    private val texture = Texture("statsScroll.png")

    private val statsSprite = Sprite(TextureRegion(texture))
        .also {
            it.setCenter(position.x + width / 2, position.y + height / 2)
            it.setScale(scale)
        }

    val height =
        (texture.height.toFloat() / texture.width.toFloat() * width.toFloat()).toInt()

    private val scale = width.toFloat() / texture.width.toFloat()

    private val xIndent = position.x + width / 4.5f

    private val font = BitmapFont()
        .also {
            it.data.setScale(scale * 3)
            it.setColor(0f, 0f, 0f, 1f)
        }

    override fun render(sb: SpriteBatch) {
        unit?.let { unit ->
            statsSprite.draw(sb)
            font.draw(
                sb,
                "[ ${unit.name} ]",
                xIndent,
                position.y + height * 13.5f / 16
            )
            font.draw(
                sb,
                "HP:\t${unit.currentStats.health}/${unit.currentStats.maxHealth}",
                xIndent,
                position.y + height * 12 / 16
            )
            font.draw(
                sb,
                "Attack:\t${unit.currentStats.attack}",
                xIndent,
                position.y + height * 11 / 16
            )
            font.draw(
                sb,
                "Defense:\t${unit.currentStats.defense}",
                xIndent,
                position.y + height * 10 / 16
            )
            font.draw(
                sb,
                "Speed:\t${unit.currentStats.speed}",
                xIndent,
                position.y + height * 9 / 16
            )
            font.draw(
                sb,
                "Range:\t${unit.currentStats.range}",
                xIndent,
                position.y + height * 8 / 16
            )
            if (unit.currentStats.isFlying) {
                font.draw(
                    sb,
                    "Flying",
                    xIndent,
                    position.y + height * 7 / 16
                )
            }
        }
    }

    override fun dispose() {
        texture.dispose()
        font.dispose()
    }
}
