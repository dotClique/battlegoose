package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle
import com.badlogic.gdx.utils.Align
import se.battlegoo.battlegoose.Game

class CreateLobbyView : ViewBase() {

    private val background = Texture("menuBackground.jpg")

    private var stage = Stage()

    private var skin: Skin = Skin(Gdx.files.internal("star-soldier-ui.json"))
    private var textField: TextField = TextField("", skin)
    private var textFieldStyle = skin.get(TextFieldStyle::class.java)

    private val title: Label = Label("Create Lobby", skin)
    private var label: Label = Label("Lobby ID: ", skin)
    private var mainMenuButton: TextButton = TextButton("Main Menu", skin)
    private var create: TextButton = TextButton("Create", skin)
    private var waiting: Label = Label("Waiting for opponent", skin)

    private val x0: Float = Menu.SPACER
    private val y0: Float = Menu.BOTTOM_SPACING

    private var created: Boolean = false

    init {
        Gdx.input.inputProcessor = stage

        textField.alignment = Align.center
        textField.height = Game.HEIGHT / 12f
        textField.width = Game.WIDTH / 5f
        textField.isDisabled = true
        textFieldStyle.font.data.setScale(2.6f)
        textField.text = "abcdef" // Insert random lobby id generated here

        mainMenuButton.width = Menu.BUTTON_WIDTH.toFloat()
        mainMenuButton.height *= 2

        create.width = Menu.BUTTON_WIDTH / 1.3f
        create.height *= 2
        create.setPosition(
            Game.WIDTH / 2f + textField.width * 0.56f,
            Game.HEIGHT / 1.75f
        )

        waiting.setPosition(
            Game.WIDTH / 2f - waiting.width * 1.3f,
            Game.HEIGHT * 0.8f
        )
    }

    fun backToMainMenu(): Boolean {
        return mainMenuButton.isPressed
    }

    fun handleInput() {
        if (create.isPressed) {
            created = true
        }
        /*
        mainMenuButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                GameStateManager.push(MainMenuState())
            }
        })
         */
    }

    fun resetWaitingText() {
        waiting.setText("Waiting for opponent")
    }

    fun updateWaitingText() {
        waiting.setText("${waiting.text}.")
    }

    override fun render(sb: SpriteBatch) {
        stage.addActor(textField)
        stage.addActor(mainMenuButton)
        stage.addActor(create)
        stage.addActor(label)

        textField.setPosition(
            Game.WIDTH / 2f - textField.width / 2f,
            Game.HEIGHT / 1.7f
        )

        title.setFontScale(5f)
        title.setPosition(
            (Game.WIDTH / 2f) - (title.width * 5f / 2f),
            Game.HEIGHT * 0.9f
        )

        label.setPosition(
            Game.WIDTH / 2f - textField.width * 1.3f,
            Game.HEIGHT / 1.6f
        )

        mainMenuButton.setPosition(x0, y0)

        sb.draw(background, 0f, 0f, Game.WIDTH.toFloat(), Game.HEIGHT.toFloat())

        title.draw(sb, 1f)
        label.draw(sb, 1f)
        textField.draw(sb, 1f)

        if (!created) {
            create.draw(sb, 1f)
        } else {
            waiting.draw(sb, 1f)
        }

        mainMenuButton.draw(sb, 1f)
    }

    override fun dispose() {
        background.dispose()
        stage.dispose()
    }
}
