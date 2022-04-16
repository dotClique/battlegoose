package se.battlegoo.battlegoose.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import se.battlegoo.battlegoose.Game
import kotlin.jvm.JvmStatic

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        LwjglApplication(Game(), config)
        config.width = Game.WIDTH.toInt()
        config.height = Game.HEIGHT.toInt()
        config.title = Game.TITLE
    }
}
