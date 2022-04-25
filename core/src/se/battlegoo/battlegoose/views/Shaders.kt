package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShaderProgram

sealed class Shaders(
    vertexShaders: VertexShaders,
    fragmentShaders: FragmentShaders
) : ShaderProgram(
    Gdx.files.internal(vertexShaders.path),
    Gdx.files.internal(fragmentShaders.path)
) {

    object RedShift : Shaders(
        VertexShaders.NOOP,
        FragmentShaders.RED_SHIFT
    )

    object GreenShift : Shaders(
        VertexShaders.NOOP,
        FragmentShaders.GREEN_SHIFT
    )

    object BlueShift : Shaders(
        VertexShaders.NOOP,
        FragmentShaders.BLUE_SHIFT
    )

    object Grayscale : Shaders(
        VertexShaders.NOOP,
        FragmentShaders.GRAYSCALE
    )
}

enum class VertexShaders(val path: String) {
    NOOP("shaders/noop.vert"),
}

enum class FragmentShaders(val path: String) {
    BLUE_SHIFT("shaders/blueShift.frag"),
    GREEN_SHIFT("shaders/greenShift.frag"),
    RED_SHIFT("shaders/redShift.frag"),
    GRAYSCALE("shaders/grayscale.frag"),
}
