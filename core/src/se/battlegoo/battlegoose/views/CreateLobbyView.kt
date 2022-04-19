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

    private var stage = Stage(Game.viewPort)

    private var skin: Skin = Skin(Gdx.files.internal("star-soldier-ui.json"))
    private var lobbyIdTextField: TextField = TextField("", skin)
    private val textFieldStyle = skin.get(TextFieldStyle::class.java)

    private val titleLabel: Label = Label("Create Lobby", skin)
    private val lobbyIdLabel: Label = Label("Lobby ID: ", skin)
    private val mainMenuButton: TextButton = TextButton("Main Menu", skin)
    private val createButton: TextButton = TextButton("Create", skin)
    private val waitingLabel: Label = Label("Waiting for opponent", skin)
    private val lobbyInfoLabel: Label = Label(
        "Ask your friend to enter this code for the game to begin",
        skin
    )

    private val x0: Float = Menu.SPACER
    private val y0: Float = Menu.BOTTOM_SPACING

    private var created: Boolean = false

    init {
        Gdx.input.inputProcessor = stage

        lobbyIdTextField.alignment = Align.center
        lobbyIdTextField.height = Game.HEIGHT / 12f
        lobbyIdTextField.width = Game.WIDTH / 5f
        lobbyIdTextField.isDisabled = true
        textFieldStyle.font.data.setScale(2.6f)
        lobbyIdTextField.text = "abcdef" // Insert random lobby id generated here

        mainMenuButton.width = Menu.BUTTON_WIDTH.toFloat()
        mainMenuButton.height *= 2

        createButton.width = Menu.BUTTON_WIDTH / 1.3f
        createButton.height *= 2
        createButton.setPosition(
            Game.WIDTH / 2f + lobbyIdTextField.width * 0.56f,
            Game.HEIGHT / 1.75f
        )

        waitingLabel.setPosition(
            Game.WIDTH / 2f - waitingLabel.width * 1.3f,
            Game.HEIGHT * 0.8f
        )

        stage.addActor(lobbyIdTextField)
        stage.addActor(mainMenuButton)
        stage.addActor(createButton)
        stage.addActor(lobbyIdLabel)
    }

    fun backToMainMenu(): Boolean {
        return mainMenuButton.isPressed
    }

    fun handleInput() {
        if (createButton.isPressed) {
            created = true
        }
        /*
        mainMenuButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                GameStateManager.goBack()
            }
        })
         */
    }

    fun resetWaitingText() {
        waitingLabel.setText("Waiting for opponent")
    }

    fun updateWaitingText() {
        waitingLabel.setText("${waitingLabel.text}.")
    }

    override fun render(sb: SpriteBatch) {
        lobbyIdTextField.setPosition(
            Game.WIDTH / 2f - lobbyIdTextField.width / 2f,
            Game.HEIGHT / 1.7f
        )

        titleLabel.setFontScale(5f)
        titleLabel.setPosition(
            (Game.WIDTH / 2f) - (titleLabel.width * 5f / 2f),
            Game.HEIGHT * 0.9f
        )

        lobbyIdLabel.setPosition(
            Game.WIDTH / 2f - lobbyIdTextField.width * 1.3f,
            Game.HEIGHT / 1.6f
        )

        lobbyInfoLabel.setPosition(
            (Game.WIDTH / 2f) - (lobbyInfoLabel.width * 1.3f),
            Game.HEIGHT * 0.5f
        )

        mainMenuButton.setPosition(x0, y0)

        sb.draw(background, 0f, 0f, Game.WIDTH.toFloat(), Game.HEIGHT.toFloat())

        titleLabel.draw(sb, 1f)
        lobbyIdLabel.draw(sb, 1f)
        lobbyIdTextField.draw(sb, 1f)

        if (!created) {
            createButton.draw(sb, 1f)
        } else {
            waitingLabel.draw(sb, 1f)
            lobbyInfoLabel.draw(sb, 1f)
        }

        mainMenuButton.draw(sb, 1f)
    }

    override fun dispose() {
        background.dispose()
        stage.dispose()
    }
}
