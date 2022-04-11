package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle
import com.badlogic.gdx.utils.Align
import java.util.*

class CreateLobbyView : ViewBase() {

    private val background = Texture("menuBackground.jpg")

    private var skin: Skin = Skin(Gdx.files.internal("star-soldier-ui.json"))
    private var textField: TextField = TextField("", skin)
    private var textFieldStyle = skin.get(TextFieldStyle::class.java)

    private val stage = Stage()

    private var uuid = UUID.randomUUID()

    init {
        textField.messageText = "Enter an ID for your lobby"
        textField.alignment = Align.center
        textFieldStyle.font.data.setScale(2.4f)
        textField.height = Gdx.graphics.height.toFloat() / 10
        textField.width = Gdx.graphics.width.toFloat() / 2
        textField.setPosition(Gdx.graphics.width.toFloat() / 2 - textField.width / 2, Gdx.graphics.height.toFloat() / 1.5f)
        stage.addActor(textField)
        Gdx.input.inputProcessor = stage
        generateRandomLobbyId(textField)
    }

    //Gdx.input.setOnscreenKeyboardVisible(true);

    override fun render(sb: SpriteBatch) {
        sb.draw(background, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        textField.draw(sb, 1f)
        stage.draw()
        stage.act()
    }

    override fun dispose() {
        background.dispose()
    }

    private fun generateRandomLobbyId(textField: TextField) {
        textField.text = uuid.toString().slice(0..15)
    }
}
