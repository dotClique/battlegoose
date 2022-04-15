package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import se.battlegoo.battlegoose.Game

class LeaderboardView : ViewBase() {

    private var stage: Stage = Stage()
    private val skin: Skin = Skin(Gdx.files.internal("star-soldier-ui.json"))
    private var leaderboard: Table = Table()
    private var topPlayers: MutableList<String> = mutableListOf("Arne", "Per", "Gudrun",
        "Olga", "Ulvhild", "Tore", "Jalmar", "Lise", "Vidar", "Ernst")

    private var labelStyle: Label.LabelStyle = skin.get(Label.LabelStyle::class.java)

    init {
        stage.addActor(leaderboard)
        leaderboard.pad(10f).defaults().expandX().space(4f)
        addPlayersToLeaderboard(topPlayers)
        leaderboard.setPosition(
            Game.WIDTH / 2f - leaderboard.width / 2f,
            Game.HEIGHT / 1.7f - leaderboard.height / 2f
        )
        labelStyle.font.data.setScale(2.6f)
    }

    fun addPlayersToLeaderboard(topPlayers: MutableList<String>) {
        for (i in 0 until topPlayers.size) {
            leaderboard.row()
            var player = Label("${i + 1} ${topPlayers[i]}", skin)
            player.setAlignment(Align.center)
            leaderboard.add(player)
            // player.color = Color.OLIVE // consider changing color of labels
        }
    }

    override fun render(sb: SpriteBatch) {
        stage.act(Gdx.graphics.deltaTime);
        stage.draw();
    }

    override fun dispose() {
        stage.dispose()
    }
}
