package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import se.battlegoo.battlegoose.Game

class CreateLobbyView(
    private val onClickMainMenu: () -> Unit
) : ViewBase() {

    private val background = Texture("menuBackground.jpg")

    private var stage = Stage(Game.viewPort)

    private var skin: Skin = Skin(Gdx.files.internal(Skins.STAR_SOLDIER.filepath))
    private var lobbyIdTextField: TextField = TextField("", skin)

    private val titleLabel: Label = Label("Create Lobby", skin)
    private val lobbyIdLabel: Label = Label("Lobby ID: ", skin)
    private val mainMenuButton: TextButton = TextButton("Main Menu", skin)
    private val createButton: TextButton = TextButton("Create", skin)
    private val waitingLabel: Label = Label("Creating lobby", skin)
    private val lobbyInfoLabel: Label = Label(
        "Ask your friend to enter this code for the game to begin",
        skin
    )

    private var waitingText = "Creating lobby"

    private val x0: Float = Menu.SPACER
    private val y0: Float = Menu.BOTTOM_SPACING

    init {
        Gdx.input.inputProcessor = stage

        titleLabel.setAlignment(Align.center)
        lobbyIdLabel.setAlignment(Align.center)
        lobbyInfoLabel.setAlignment(Align.center)

        lobbyIdTextField.alignment = Align.center
        lobbyIdTextField.height = Game.HEIGHT / 12f
        lobbyIdTextField.width = Game.WIDTH / 5f
        lobbyIdTextField.isDisabled = true

        mainMenuButton.width = Menu.BUTTON_WIDTH.toFloat()
        mainMenuButton.height *= 1.5f

        waitingLabel.setPosition(
            Game.WIDTH / 2f - waitingLabel.width / 2f,
            Game.HEIGHT * 0.8f
        )

        stage.addActor(lobbyIdTextField)
        stage.addActor(mainMenuButton)
        stage.addActor(createButton)
        stage.addActor(lobbyIdLabel)
    }

    fun resetWaitingText() {
        waitingLabel.setText(waitingText)
    }

    fun updateWaitingText() {
        waitingLabel.setText("${waitingLabel.text}.")
    }

    fun setGeneratedLobbyId(lobbyId: String) {
        lobbyIdTextField.text = lobbyId
        waitingText = "Waiting for opponent"
    }

    override fun registerInput() {
        if (Gdx.input.justTouched() && mainMenuButton.isPressed) {
            onClickMainMenu()
        }
    }

    override fun render(sb: SpriteBatch) {
        lobbyIdTextField.setPosition(
            Game.WIDTH / 2f - lobbyIdTextField.width / 2f,
            Game.HEIGHT / 1.7f
        )

        titleLabel.setFontScale(5f)
        titleLabel.setPosition(
            (Game.WIDTH / 2f) - titleLabel.width / 2f,
            Game.HEIGHT * 0.9f
        )

        lobbyIdLabel.setPosition(
            Game.WIDTH / 2f - lobbyIdTextField.width * 1.1f,
            Game.HEIGHT / 1.66f
        )

        lobbyInfoLabel.setPosition(
            (Game.WIDTH / 2f) - (lobbyInfoLabel.width / 2f),
            Game.HEIGHT * 0.5f
        )

        mainMenuButton.setPosition(x0, y0)

        sb.draw(background, 0f, 0f, Game.WIDTH, Game.HEIGHT)

        titleLabel.draw(sb, 1f)
        lobbyIdLabel.draw(sb, 1f)
        lobbyIdTextField.draw(sb, 1f)

        waitingLabel.draw(sb, 1f)

        if (lobbyIdTextField.text != "") {
            lobbyInfoLabel.draw(sb, 1f)
        }

        mainMenuButton.draw(sb, 1f)
    }

    override fun dispose() {
        background.dispose()
        stage.dispose()
        skin.dispose()
    }
}
