package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import se.battlegoo.battlegoose.Game

class ChangeUsernameView(
    val x: Float = 0f,
    val y: Float = 0f,
    val width: Float = 700f,
    val height: Float = 100f,
    var usernameChangedListener: (String) -> Unit = {}
) : ViewBase() {

    companion object {
        private const val MAX_USERNAME_LENGTH = 15
    }

    private var skin: Skin = Skin(Gdx.files.internal("star-soldier-ui.json"))
    private val textField: TextField = TextField("", skin)
    private val saveButton = ButtonView("acceptBtn.png", x, y, height.toInt()) {
        textField.onscreenKeyboard.show(false)
        stage.unfocus(textField)

        state = ChangeUsernameState.PENDING
        usernameChangedListener(username)
    }

    private val stage = Stage(Game.viewPort)
    private var savedUsername: String = ""
    var username: String by textField::text

    private var state: ChangeUsernameState = ChangeUsernameState.PENDING
        set(value) {
            when (value) {
                ChangeUsernameState.SYNCED -> {
                    saveButton.hidden = true
                    saveButton.disabled = true
                    textField.isDisabled = false
                }
                ChangeUsernameState.EDITED -> {
                    saveButton.hidden = false
                    saveButton.disabled = false
                    saveButton.texturePath = "acceptBtn.png"
                    textField.isDisabled = false
                }
                ChangeUsernameState.PENDING -> {
                    saveButton.hidden = false
                    saveButton.disabled = true
                    saveButton.texturePath = "updatingBtn.png"
                    textField.isDisabled = true
                }
            }
            field = value
        }

    init {
        textField.maxLength = MAX_USERNAME_LENGTH
        textField.messageText = "Your username"
        textField.alignment = Align.center
        textField.width = width - saveButton.width
        textField.height = height
        textField.setPosition(x + height, y)

        stage.addActor(textField)
        Gdx.input.inputProcessor = stage

        textField.setTextFieldListener { _, _ ->
            state = if (username == savedUsername) ChangeUsernameState.SYNCED
            else ChangeUsernameState.EDITED
        }
        state = ChangeUsernameState.PENDING
    }

    fun resolveUsernameChange(newUsername: String) {
        username = newUsername
        savedUsername = newUsername
        state = ChangeUsernameState.SYNCED
    }

    override fun render(sb: SpriteBatch) {
        stage.act()
        stage.draw()
        saveButton.render(sb)
    }

    override fun dispose() {
        stage.dispose()
        saveButton.dispose()
    }

    override fun registerInput() {
        saveButton.registerInput()
    }
}

enum class ChangeUsernameState {
    SYNCED, EDITED, PENDING
}
