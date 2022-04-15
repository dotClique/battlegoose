package se.battlegoo.battlegoose.views

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import se.battlegoo.battlegoose.models.units.UnitStats

class UnitHealthBarView(
    unitStats: UnitStats,
    private val xPos: Float,
    private val yPos: Float,
    val width: Int
) : ViewBase() {

    private val height: Int = width * 1 / 8
    private var healthBar: ProgressBar
    private var progressBarStyle = ProgressBar.ProgressBarStyle()
    private val font = BitmapFont()
    var targetProgressValue: Float =
        unitStats.health.toFloat() / unitStats.maxHealth.toFloat()
    var currentHealth = unitStats.health
    var maxHealth = unitStats.maxHealth

    init {
        progressBarStyle.background = getColoredDrawable(width, height, Color.RED)
        progressBarStyle.knob = getColoredDrawable(0, height, Color.GREEN)
        progressBarStyle.knobBefore = getColoredDrawable(width, height, Color.GREEN)

        healthBar = ProgressBar(0f, 1f, 0.01f, false, progressBarStyle)
        healthBar.width = width.toFloat()
        healthBar.height = height.toFloat()

        healthBar.setAnimateDuration(0.0f)

        // setValue includes animation
        healthBar.value =
            unitStats.health.toFloat() / unitStats.maxHealth.toFloat()

        healthBar.setAnimateDuration(0.2f)

        healthBar.setOrigin(xPos + width / 2, yPos + height / 2)
        healthBar.x = xPos
        healthBar.y = yPos

        font.data.setScale(width.toFloat() / 180)
        font.setColor(1f, 1f, 1f, 9f)
    }

    // Could be moved to a Utils class?
    private fun getColoredDrawable(width: Int, height: Int, color: Color?): Drawable? {
        val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)
        pixmap.setColor(color)
        pixmap.fill()
        val drawable = TextureRegionDrawable(TextureRegion(Texture(pixmap)))
        pixmap.dispose()
        return drawable
    }

    private fun step() {
        if (targetProgressValue > healthBar.value - healthBar.stepSize) {
            healthBar.value = targetProgressValue
        } else if (targetProgressValue < healthBar.value + healthBar.stepSize) {
            healthBar.value -= healthBar.stepSize
        }
    }

    override fun render(sb: SpriteBatch) {

        step()
        healthBar.updateVisualValue()
        healthBar.draw(sb, 1f)
        font.draw(
            sb,
            "HP:\t$currentHealth/$maxHealth",
            xPos + width * 3 / 10,
            yPos + height * 2 / 3
        )
    }

    override fun dispose() {
        font.dispose()
    }
}
