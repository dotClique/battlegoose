package com.progark.battlegoose

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.ScreenUtils
import gamestates.GameStateManager
import gamestates.MainMenuState

class Game : ApplicationAdapter() {
    private lateinit var batch: SpriteBatch

    override fun create() {
        batch = SpriteBatch()
        GameStateManager.push(MainMenuState())
    }

    override fun render() {
        ScreenUtils.clear(1f, 1f, 1f, 1f)
        GameStateManager.update(Gdx.graphics.deltaTime)
        GameStateManager.render(batch)

    }

    override fun dispose() {
        batch.dispose()
    }


}