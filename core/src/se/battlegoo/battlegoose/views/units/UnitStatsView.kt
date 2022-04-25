package se.battlegoo.battlegoose.views.units

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import se.battlegoo.battlegoose.datamodels.ScreenVector
import se.battlegoo.battlegoose.models.units.UnitModel
import se.battlegoo.battlegoose.views.utils.ViewBase

class UnitStatsView(
    private val position: ScreenVector,
    val width: Float
) : ViewBase() {

    var unit: UnitModel? = null

    private val xIndent = position.x + width / 4.4f

    private val texture = Texture("statsScrollCompact.png")
    private val rolledTexture = Texture("statsScrollCompactRolled.png")

    val height =
        (texture.height.toFloat() / texture.width.toFloat() * width).toInt()

    private val scale = width / texture.width.toFloat()

    private val statsSprite = Sprite(TextureRegion(texture))
        .also {
            it.setCenter(position.x + width / 2, position.y + height / 2)
            it.setScale(scale)
        }

    private val rolledSprite = Sprite(TextureRegion(rolledTexture))
        .also {
            it.setCenter(position.x + width / 2, position.y + height / 2)
            it.setScale(scale)
        }

    private val font = BitmapFont()
        .also {
            it.data.setScale(scale * 3)
            it.setColor(0f, 0f, 0f, 1f)
        }

    override fun render(sb: SpriteBatch) {
        unit.let { unit ->
            if (unit == null) {
                rolledSprite.draw(sb)
                return
            }
            statsSprite.draw(sb)
            font.draw(
                sb,
                "[ ${unit.name} ]",
                xIndent,
                position.y + height * 13 / 16
            )
            font.draw(
                sb,
                "HP: ${unit.currentStats.health}/${unit.currentStats.maxHealth}",
                xIndent,
                position.y + height * 11.5f / 16
            )
            font.draw(
                sb,
                "Attack: ${unit.currentStats.attack}",
                xIndent,
                position.y + height * 10 / 16
            )
            font.draw(
                sb,
                "Defense: ${unit.currentStats.defense}",
                xIndent,
                position.y + height * 8.5f / 16
            )
            font.draw(
                sb,
                "Speed: ${unit.currentStats.speed}",
                xIndent,
                position.y + height * 7 / 16
            )
            font.draw(
                sb,
                "Range: ${unit.currentStats.range}",
                xIndent,
                position.y + height * 5.5f / 16
            )
            if (unit.currentStats.isFlying) {
                font.draw(
                    sb,
                    "Flying",
                    xIndent,
                    position.y + height * 4 / 16
                )
            }
        }
    }

    override fun dispose() {
        texture.dispose()
        rolledTexture.dispose()
        font.dispose()
    }
}
