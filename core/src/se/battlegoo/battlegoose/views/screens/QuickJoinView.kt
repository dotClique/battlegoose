package se.battlegoo.battlegoose.views.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.network.RandomPairingStatus
import se.battlegoo.battlegoose.utils.TextureAsset
import se.battlegoo.battlegoose.views.utils.DotWaitingLabelView
import se.battlegoo.battlegoose.views.utils.MenuLayout
import se.battlegoo.battlegoose.views.utils.Skins
import se.battlegoo.battlegoose.views.utils.ViewBase

class QuickJoinView(
    private val onClickMainMenu: () -> Unit,
    private val stage: Stage
) : ViewBase() {

    private val background = Game.getTexture(TextureAsset.MENU_BACKGROUND)

    private val skin: Skin = Skin(Gdx.files.internal(Skins.STAR_SOLDIER.filepath))

    private val mainMenuButton: TextButton = TextButton("Main Menu", skin)
    private val titleLabel: Label = Label("Quick Join", skin)
    private val statusLabel: DotWaitingLabelView = DotWaitingLabelView("", skin)

    private val x0: Float = MenuLayout.SPACER
    private val y0: Float = MenuLayout.BOTTOM_SPACING

    init {

        mainMenuButton.width = MenuLayout.BUTTON_WIDTH.toFloat()
        mainMenuButton.height *= 1.5f
        mainMenuButton.setPosition(x0, y0)

        titleLabel.setAlignment(Align.center)
        titleLabel.setFontScale(5f)
        titleLabel.setPosition(
            (Game.WIDTH / 2f) - titleLabel.width / 2f,
            Game.HEIGHT * 0.9f
        )

        statusLabel.setPosition(
            Game.WIDTH / 2f - statusLabel.width / 2f,
            Game.HEIGHT * 0.6f
        )
        stage.addActor(mainMenuButton)
        stage.addActor(titleLabel)
        statusLabel.shouldDotLoad = true
    }

    fun setStatus(status: RandomPairingStatus) {
        statusLabel.setText(
            when (status) {
                is RandomPairingStatus.WaitingInQueue ->
                    "Waiting in queue"
                is RandomPairingStatus.StartBattle ->
                    "Starting battle"
                is RandomPairingStatus.WaitingForOtherPlayer ->
                    "Waiting for other player"
                is RandomPairingStatus.Failed ->
                    "Failed to load"
                else -> {
                    "Loading"
                }
            }
        )
    }
    override fun registerInput() {
        if (Gdx.input.justTouched() && mainMenuButton.isPressed) {
            onClickMainMenu()
        }
    }

    override fun render(sb: SpriteBatch) {
        sb.draw(background, 0f, 0f, Game.WIDTH, Game.HEIGHT)
        statusLabel.render(sb)
    }

    override fun dispose() {
        skin.dispose()
    }
}
