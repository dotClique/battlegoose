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
import se.battlegoo.battlegoose.network.CreateLobbyStatus
import se.battlegoo.battlegoose.utils.TextureAsset

class CreateLobbyView(
    private val onClickMainMenu: () -> Unit,
    val stage: Stage
) : ViewBase() {

    // Assets
    private val background = Game.getTexture(TextureAsset.MENU_BACKGROUND)
    private var skin: Skin = Skin(Gdx.files.internal(Skins.STAR_SOLDIER.filepath))

    // Labels and TextFields
    private var lobbyIdTextField: TextField = TextField("", skin)
    private val titleLabel: Label = Label("Create Lobby", skin)
    private val lobbyIdLabel: Label = Label("Lobby ID: ", skin)

    //    private val statusLabel: Label = Label("Creating lobby", skin)
    private var statusLabel: DotWaitingLabelView = DotWaitingLabelView(
        "Creating lobby", skin,
    )
    private val lobbyInfoLabel: Label = Label(
        "Ask your friend to enter this code for the game to begin",
        skin
    )

    // Buttons
    private val mainMenuButton: TextButton = TextButton("Main Menu", skin)
    private val startBattleButton: TextButton = TextButton("Start Battle", skin)

    // Dynamically set onClickStartBattle function
    var onClickStartBattle: (() -> Unit)? = null
        set(value) {
            field = value
            if (value == null) {
                startBattleButton.remove()
                stage.addActor(lobbyInfoLabel)

            } else {
                stage.addActor(startBattleButton)
                lobbyInfoLabel.remove()
            }
        }


    private val x0: Float = Menu.SPACER
    private val y0: Float = Menu.BOTTOM_SPACING

    init {
        titleLabel.setAlignment(Align.center)
        lobbyIdLabel.setAlignment(Align.center)
        lobbyInfoLabel.setAlignment(Align.center)

        lobbyIdTextField.alignment = Align.center
        lobbyIdTextField.height = Game.HEIGHT / 12f
        lobbyIdTextField.width = Game.WIDTH / 5f
        lobbyIdTextField.isDisabled = true

        mainMenuButton.width = Menu.BUTTON_WIDTH.toFloat()
        startBattleButton.width = Menu.BUTTON_WIDTH.toFloat()
        mainMenuButton.height *= 1.5f
        startBattleButton.height *= 1.5f
        titleLabel.setFontScale(5f)

        lobbyIdTextField.setPosition(
            Game.WIDTH / 2f - lobbyIdTextField.width / 2f,
            Game.HEIGHT / 1.7f
        )
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
        statusLabel.setPosition(
            Game.WIDTH / 2f - statusLabel.width / 2f,
            Game.HEIGHT * 0.8f
        )
        mainMenuButton.setPosition(x0, y0)
        startBattleButton.setPosition(Game.WIDTH - x0 - startBattleButton.width, y0)

        stage.addActor(lobbyIdTextField)
        stage.addActor(mainMenuButton)
        stage.addActor(lobbyIdLabel)
        stage.addActor(titleLabel)
        stage.addActor(lobbyInfoLabel)
    }

    fun setStatus(status: CreateLobbyStatus) {
        statusLabel.setText(
            when (status) {
                CreateLobbyStatus.OTHER_PLAYER_JOINED -> {
                    statusLabel.shouldDotLoad = false
                    "Another player has joined. Ready to start battle"
                }
                CreateLobbyStatus.OPEN -> {
                    statusLabel.shouldDotLoad = true
                    "Waiting for another player"
                }
            }
        )

    }

    fun setGeneratedLobbyId(lobbyId: String) {
        lobbyIdTextField.text = lobbyId
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
        // The stage and its actors is drawn globally

        sb.draw(background, 0f, 0f, Game.WIDTH, Game.HEIGHT)
        statusLabel.render(sb)
    }

    override fun dispose() {
        skin.dispose()
    }
}
