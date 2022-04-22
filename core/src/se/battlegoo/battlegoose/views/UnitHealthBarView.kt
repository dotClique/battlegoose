package se.battlegoo.battlegoose.views

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.utils.Align
import se.battlegoo.battlegoose.ScreenVector
import se.battlegoo.battlegoose.utils.createDrawableOfTexture
import se.battlegoo.battlegoose.utils.createSolidColorTexture
import se.battlegoo.battlegoose.utils.emptyDrawable

class UnitHealthBarView(
    position: ScreenVector,
    width: Float
) : ViewBase() {

    var position = position
        set(position) {
            field = position
            updateHealthBarDrawing()
        }

    var width = width
        set(width) {
            field = width
            updateHealthBarDrawing()
        }

    private val height: Float
        get() = width * 0.14f

    private val healthColor = Color.valueOf("#64DD17")
    private val healthMissingColor = Color.valueOf("#E74C3C")
    private var healthBarStyle = ProgressBar.ProgressBarStyle()
    private var healthColorTexture: Texture? = null
    private var healthMissingColorTexture: Texture? = null
    private var healthBar: ProgressBar =
        ProgressBar(0f, 1f, 0.01f, false, healthBarStyle)
            .also { it.value = it.maxValue } // initial value to avoid startup animation

    private val font = BitmapFont().also { it.setColor(1f, 1f, 1f, 1f) }

    var currentHealth = 0
        set(value) {
            field = value
            updateHealthProgress()
        }
    var maxHealth = 0
        set(value) {
            field = value
            updateHealthProgress()
        }

    private fun updateHealthProgress() {
        healthBar.value = if (maxHealth == 0) {
            0f // prevent division by zero
        } else if (currentHealth == maxHealth) {
            1f // prevent floating point error
        } else {
            currentHealth / maxHealth.toFloat()
        }
    }

    private val animationDuration = 0.2f
    private val fakeTimePerRender = animationDuration / 10 // since we lack the real one

    init {
        healthBarStyle.knob = emptyDrawable() // hack to hide knob without breaking progress pos
        healthBar.setAnimateDuration(animationDuration)
        updateHealthBarDrawing()
    }

    private fun updateHealthBarDrawing() {
        createSolidColorTexture(width, height, healthColor).let { texture ->
            healthColorTexture?.dispose()
            healthColorTexture = texture
            healthBarStyle.knobBefore = createDrawableOfTexture(texture)
        }
        createSolidColorTexture(width, height, healthMissingColor).let { texture ->
            healthMissingColorTexture?.dispose()
            healthMissingColorTexture = texture
            healthBarStyle.background = createDrawableOfTexture(texture)
        }

        healthBar.width = width
        healthBar.height = height
        healthBar.x = position.x
        healthBar.y = position.y

        font.data.setScale(width / 120)
    }

    override fun render(sb: SpriteBatch) {
        healthBar.act(fakeTimePerRender)
        healthBar.draw(sb, 1f)
        font.draw(
            sb,
            "$currentHealth/$maxHealth",
            position.x,
            position.y + height * 5 / 6,
            healthBar.width,
            Align.center,
            false
        )
    }

    override fun dispose() {
        font.dispose()
        healthColorTexture?.dispose()
        healthMissingColorTexture?.dispose()
    }
}
