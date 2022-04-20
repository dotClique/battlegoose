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

class JoinLobbyView(
    private val onClickMainMenu: () -> Unit
) : ViewBase() {

    private val background = Texture("menuBackground.jpg")

    private var stage = Stage(Game.viewPort)

    private var skin: Skin = Skin(Gdx.files.internal("star-soldier-ui.json"))
    private var lobbyIdTextField: TextField = TextField("", skin)

    private val titleLabel: Label = Label("Join Lobby", skin)
    private val lobbyIdLabel: Label = Label("Lobby ID: ", skin)
    private val mainMenuButton: TextButton = TextButton("Main Menu", skin)
    private val joinButton: TextButton = TextButton("Join", skin)
    private val waitingLabel: Label = Label("", skin)

    private val x0: Float = Menu.SPACER
    private val y0: Float = Menu.BOTTOM_SPACING

    private var joined: Boolean = false

    init {
        Gdx.input.inputProcessor = stage

        titleLabel.setAlignment(Align.center)
        lobbyIdLabel.setAlignment(Align.center)

        lobbyIdTextField.alignment = Align.center
        lobbyIdTextField.height = Game.HEIGHT / 12f
        lobbyIdTextField.width = Game.WIDTH / 5f
        // textFieldStyle.font.data.setScale(2.6f)

        mainMenuButton.width = Menu.BUTTON_WIDTH.toFloat()
        mainMenuButton.height *= 1.5f

        joinButton.width = Menu.BUTTON_WIDTH / 1.3f
        joinButton.height *= 1.5f
        joinButton.setPosition(
            Game.WIDTH / 2f + lobbyIdTextField.width * 0.56f,
            Game.HEIGHT / 1.75f
        )

        waitingLabel.setPosition(
            Game.WIDTH / 2f - waitingLabel.width / 2f,
            Game.HEIGHT * 0.8f
        )

        stage.addActor(lobbyIdTextField)
        stage.addActor(mainMenuButton)
        stage.addActor(joinButton)
        stage.addActor(lobbyIdLabel)
    }

    fun getJoinLobbyId(): String {
        return lobbyIdTextField.text
    }

    fun resetWaitingText() {
        waitingLabel.setText("Waiting for opponent")
    }

    fun updateWaitingText() {
        waitingLabel.setText("${waitingLabel.text}.")
    }

    override fun registerInput() {
        if (Gdx.input.justTouched() && mainMenuButton.isPressed) {
            onClickMainMenu()
        }
    }

    fun handleInput() {
        if (joinButton.isPressed) {
            lobbyIdTextField.isDisabled = true
            joined = true
        }
    }

    override fun render(sb: SpriteBatch) {
        lobbyIdTextField.setPosition(
            Game.WIDTH / 2f - lobbyIdTextField.width / 2f,
            Game.HEIGHT / 1.7f
        )

        titleLabel.setFontScale(5f)
        titleLabel.setPosition(
            (Game.WIDTH / 2f) - (titleLabel.width / 2f),
            Game.HEIGHT * 0.9f
        )

        lobbyIdLabel.setPosition(
            Game.WIDTH / 2f - lobbyIdTextField.width * 1.1f,
            Game.HEIGHT / 1.66f
        )

        mainMenuButton.setPosition(x0, y0)

        sb.draw(background, 0f, 0f, Game.WIDTH, Game.HEIGHT)

        titleLabel.draw(sb, 1f)
        lobbyIdLabel.draw(sb, 1f)
        lobbyIdTextField.draw(sb, 1f)

        if (!joined) {
            joinButton.draw(sb, 1f)
        } else {
            waitingLabel.draw(sb, 1f)
        }

        mainMenuButton.draw(sb, 1f)
    }

    override fun dispose() {
        background.dispose()
        stage.dispose()
        skin.dispose()
    }
}
