package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextField

class CreateLobbyView : ViewBase() {

    private val background = Texture("createLobbyBackground.png")

    private var skin: Skin = Skin(Gdx.files.internal("uiskin.json"))
    private var textField: TextField = TextField("", skin)

    init {
        textField.messageText = "Enter an ID for your lobby"
        textField.setPosition(0f, 0f)
    }

    //Gdx.input.setOnscreenKeyboardVisible(true);

    override fun render(sb: SpriteBatch) {
        sb.draw(background, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    }

    override fun dispose() {
        background.dispose()
    }
}
