package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShaderProgram

object RedShiftShader : ShaderProgram(
    // No-op vertex shader
    Gdx.files.internal("shaders/noop.vert"),
    // Red-shifting fragment shader
    Gdx.files.internal("shaders/redShift.frag")
)
