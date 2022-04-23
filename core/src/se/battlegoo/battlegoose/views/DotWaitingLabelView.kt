package se.battlegoo.battlegoose.views

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align

class DotWaitingLabelView(
    text: String,
    skin: Skin,
    private var xPos: Float = 0f,
    private var yPos: Float = 0f,
    var shouldDotLoad: Boolean = false
) : ViewBase() {
    private var waitingTimer: Float = 0f
    private val letterSpawnTime: Float = 1f
    private var letterCount: Int = 0
    private val statusLabel = Label(text, skin)
    var width = statusLabel.width
        private set
    var height = statusLabel.height
        private set

    init {
        statusLabel.setAlignment(Align.center)
        statusLabel.setPosition(xPos, yPos)
    }

    private fun resetWaitingText() {
        statusLabel.setText(statusLabel.text.split(".")[0])
    }

    private fun updateWaitingText() {
        statusLabel.setText("${statusLabel.text}.")
    }

    fun setText(text: String) {
        statusLabel.setText(text)
    }

    fun setPosition(x: Float, y: Float) {
        statusLabel.setPosition(x, y)
    }

    private fun dotLoading() {
        waitingTimer += 0.01f
        if (letterCount >= 4f) {
            resetWaitingText()
            letterCount = 0
        } else if (waitingTimer > letterSpawnTime) {
            updateWaitingText()
            waitingTimer -= letterSpawnTime
            letterCount++
        }
    }

    override fun render(sb: SpriteBatch) {
        width = statusLabel.width
        height = statusLabel.height
        if (shouldDotLoad) {
            dotLoading()
        }
        statusLabel.draw(sb, 1f)
    }

    override fun dispose() {
    }
}
