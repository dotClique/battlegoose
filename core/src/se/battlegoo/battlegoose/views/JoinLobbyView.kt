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
import se.battlegoo.battlegoose.network.JoinLobbyStatus
import se.battlegoo.battlegoose.utils.TextureAsset

class JoinLobbyView(
    private val onClickMainMenu: () -> Unit,
    private val onJoinLobby: (String) -> Unit,
    private val stage: Stage,
) : ViewBase() {


    private val background = Game.getTexture(TextureAsset.MENU_BACKGROUND)

    private var skin: Skin = Skin(Gdx.files.internal(Skins.STAR_SOLDIER.filepath))
    private var lobbyIdTextField: TextField = TextField("", skin)

    private val titleLabel: Label = Label("Join Lobby", skin)
    private val lobbyIdLabel: Label = Label("Lobby ID: ", skin)
    private val mainMenuButton: TextButton = TextButton("Main Menu", skin)
    private val joinButton: TextButton = TextButton("Join", skin)
    private val statusLabel: DotWaitingLabelView = DotWaitingLabelView("", skin)

    private val x0: Float = Menu.SPACER
    private val y0: Float = Menu.BOTTOM_SPACING

    private var joined: Boolean = false
        set(value) {
            field = value
            lobbyIdTextField.isDisabled = !value
        }

    init {
        titleLabel.setAlignment(Align.center)
        lobbyIdLabel.setAlignment(Align.center)

        lobbyIdTextField.alignment = Align.center
        lobbyIdTextField.height = Game.HEIGHT / 12f
        lobbyIdTextField.width = Game.WIDTH / 5f

        mainMenuButton.width = Menu.BUTTON_WIDTH.toFloat()
        mainMenuButton.height *= 1.5f

        joinButton.width = Menu.BUTTON_WIDTH / 1.3f
        joinButton.height *= 1.5f
        titleLabel.setFontScale(5f)
        joinButton.setPosition(
            Game.WIDTH / 2f + lobbyIdTextField.width * 0.56f,
            Game.HEIGHT / 1.75f
        )
        lobbyIdTextField.setPosition(
            Game.WIDTH / 2f - lobbyIdTextField.width / 2f,
            Game.HEIGHT / 1.7f
        )
        titleLabel.setPosition(
            (Game.WIDTH / 2f) - (titleLabel.width / 2f),
            Game.HEIGHT * 0.9f
        )
        lobbyIdLabel.setPosition(
            Game.WIDTH / 2f - lobbyIdTextField.width * 1.1f,
            Game.HEIGHT / 1.66f
        )
        statusLabel.setPosition(
            (Game.WIDTH - statusLabel.width) / 2f,
            Game.HEIGHT * 0.8f
        )
        mainMenuButton.setPosition(x0, y0)

        stage.addActor(lobbyIdTextField)
        stage.addActor(mainMenuButton)
        stage.addActor(joinButton)
        stage.addActor(lobbyIdLabel)
        stage.addActor(titleLabel)
    }

    fun setStatus(status: JoinLobbyStatus) {
        statusLabel.setText(
            when (status) {
                is JoinLobbyStatus.Ready -> {
                    statusLabel.shouldDotLoad = true
                    "Waiting for opponent to start battle"
                }
                is JoinLobbyStatus.StartBattle -> {
                    statusLabel.shouldDotLoad = true
                    "Starting battle"
                }
                is JoinLobbyStatus.NotAccessible -> {
                    statusLabel.shouldDotLoad = false
                    "Lobby was not accessible"
                }
                is JoinLobbyStatus.Full -> {
                    statusLabel.shouldDotLoad = false
                    "Lobby is full. Try another lobby"
                }
                is JoinLobbyStatus.DoesNotExist -> {
                    statusLabel.shouldDotLoad = false
                    "The requested lobby does not exist"
                }
            }
        )
    }

    override fun registerInput() {
        if (Gdx.input.justTouched()) {
            when {
                mainMenuButton.isPressed -> onClickMainMenu()
                joinButton.isPressed -> {
                    onJoinLobby(lobbyIdTextField.text.uppercase())
                    joined = true
                }
            }
        }
    }

    override fun render(sb: SpriteBatch) {
        sb.draw(background, 0f, 0f, Game.WIDTH, Game.HEIGHT)
        statusLabel.render(sb)
    }

    override fun dispose() {
        stage.dispose()
        skin.dispose()
    }
}
