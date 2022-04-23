package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.utils.TextureAsset

class CreateLobbyView(
    private val onClickMainMenu: () -> Unit,
    val stage: Stage
) : ViewBase() {

    private var waitingTimer: Float = 0f
    private val letterSpawnTime: Float = 1f
    private var letterCount: Int = 0

    var onClickStartBattle: (() -> Unit)? = null

    private val background = Game.getTexture(TextureAsset.MENU_BACKGROUND)

    private var skin: Skin = Skin(Gdx.files.internal(Skins.STAR_SOLDIER.filepath))
    private var lobbyIdTextField: TextField = TextField("", skin)

    private val titleLabel: Label = Label("Create Lobby", skin)
    private val lobbyIdLabel: Label = Label("Lobby ID: ", skin)
    private val mainMenuButton: TextButton = TextButton("Main Menu", skin)
    private val waitingLabel: Label = Label("Creating lobby", skin)
    private val lobbyInfoLabel: Label = Label(
        "Ask your friend to enter this code for the game to begin",
        skin
    )

    private val startBattleButton: TextButton = TextButton("Start Battle", skin)

    private var waitingText = "Creating lobby"

    private val x0: Float = Menu.SPACER
    private val y0: Float = Menu.BOTTOM_SPACING

    init {
        titleLabel.setAlignment(Align.center)
        lobbyIdLabel.setAlignment(Align.center)
        lobbyInfoLabel.setAlignment(Align.center)
        waitingLabel.setAlignment(Align.center)

        lobbyIdTextField.alignment = Align.center
        lobbyIdTextField.height = Game.HEIGHT / 12f
        lobbyIdTextField.width = Game.WIDTH / 5f
        lobbyIdTextField.isDisabled = true

        mainMenuButton.width = Menu.BUTTON_WIDTH.toFloat()
        startBattleButton.width = Menu.BUTTON_WIDTH.toFloat()
        mainMenuButton.height *= 1.5f
        startBattleButton.height *= 1.5f

        stage.addActor(lobbyIdTextField)
        stage.addActor(mainMenuButton)
        stage.addActor(lobbyIdLabel)
    }

    private fun resetWaitingText() {
        waitingLabel.setText(waitingLabel.text.split(".")[0])
    }

    private fun updateWaitingText() {
        waitingLabel.setText("${waitingLabel.text}.")
    }

    fun setStatusText(text: String) {
        waitingLabel.setText(text)
    }

    fun setGeneratedLobbyId(lobbyId: String) {
        lobbyIdTextField.text = lobbyId
        waitingText = "Waiting for opponent"
    }

    override fun registerInput() {
        if (Gdx.input.justTouched() && mainMenuButton.isPressed) {
            onClickMainMenu()
        }
        if (Gdx.input.justTouched() && startBattleButton.isPressed) {
            onClickStartBattle?.invoke()
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
        waitingLabel.setPosition(
            Game.WIDTH / 2f - waitingLabel.width / 2f,
            Game.HEIGHT * 0.8f
        )

        mainMenuButton.setPosition(x0, y0)
        startBattleButton.setPosition(Game.WIDTH - x0 - startBattleButton.width, y0)

        sb.draw(background, 0f, 0f, Game.WIDTH, Game.HEIGHT)

        titleLabel.draw(sb, 1f)
        lobbyIdLabel.draw(sb, 1f)
        lobbyIdTextField.draw(sb, 1f)

        waitingLabel.draw(sb, 1f)

        if (lobbyIdTextField.text != "") {
            lobbyInfoLabel.draw(sb, 1f)
        }

        mainMenuButton.draw(sb, 1f)

        if (onClickStartBattle == null)
            startBattleButton.remove()
        else
            stage.addActor(startBattleButton)

        // Loading with dots.
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

    override fun dispose() {
        skin.dispose()
    }
}
