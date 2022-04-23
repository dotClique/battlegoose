package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.network.ListenerCanceler
import se.battlegoo.battlegoose.network.MultiplayerService
import se.battlegoo.battlegoose.network.RandomOpponentStatus

class QuickJoinState : GameState() {

    private val background = Texture("placeholder.png")

    private val title: BitmapFont = BitmapFont()
    private var randomOpponentStatus: RandomOpponentStatus = RandomOpponentStatus.JOIN_QUEUE
    private val layoutTitle = GlyphLayout(title, randomOpponentStatus.toString())

    private val goBack: BitmapFont = BitmapFont()
    private val goBackText = "Press anywhere to return to main menu..."
    private val layoutGoBack = GlyphLayout(goBack, goBackText)
    private var queueCanceler: ListenerCanceler? = null
    private var lobbyID: String = ""

    private var cancelStartBattleListener: ListenerCanceler = {}

    init {
        MultiplayerService.tryRequestOpponent({
            randomOpponentStatus = it
        }, { lobbyID ->
            this.lobbyID = lobbyID
        }, {
            queueCanceler = it
        }, { cancelListener ->
            this.cancelStartBattleListener = cancelListener
        })
    }

    private fun handleInput() {
        if (Gdx.input.justTouched()) {
            cancelStartBattleListener()
            MultiplayerService.tryCancelRequestOpponent({ success ->
                if (success) {
                    GameStateManager.push(MainMenuState())
                }
            }, queueCanceler)
        }
    }

    override fun update(dt: Float) {
        handleInput()
        if (randomOpponentStatus == RandomOpponentStatus.JOINED_LOBBY) {
            MultiplayerService.listenForBattleStart(this.lobbyID, {}, {})
            GameStateManager.replace(BattleState())
        }
        if (randomOpponentStatus == RandomOpponentStatus.OTHER_PLAYER_JOINED) {
            MultiplayerService.startBattle(this.lobbyID)
            GameStateManager.replace(BattleState())
        }
    }

    override fun render(sb: SpriteBatch) {
        sb.draw(background, 0f, 0f, Game.WIDTH.toFloat(), Game.HEIGHT.toFloat())

        title.data.setScale(5f)
        title.draw(
            sb, randomOpponentStatus.toString(), (Game.WIDTH / 2f) - (layoutTitle.width * 5f / 2f),
            (Game.HEIGHT * 0.9f) + layoutTitle.height * 3f
        )

        goBack.data.setScale(3f)
        goBack.draw(
            sb, goBackText, Game.WIDTH / 20f - (layoutGoBack.width / 3f),
            Game.HEIGHT / 20f + layoutGoBack.height * 3f
        )
    }

    override fun dispose() {
        background.dispose()
        title.dispose()
        goBack.dispose()
    }
}
