package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.controllers.HeroSelectionController
import se.battlegoo.battlegoose.models.heroes.AdmiralAlbatross
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.models.heroes.HeroSelection
import se.battlegoo.battlegoose.models.heroes.MajorMallard
import se.battlegoo.battlegoose.models.heroes.SergeantSwan
import se.battlegoo.battlegoose.views.screens.HeroSelectionView
import se.battlegoo.battlegoose.views.screens.HeroSelectionViewModel

class HeroSelectionState(
    private val createLobbyState: (Hero) -> LobbyState
) : GameState() {

    private val heroes: List<Hero> = listOf(SergeantSwan(), MajorMallard(), AdmiralAlbatross())
    private val heroSelection = HeroSelection(heroes)
    private val heroSelectionView = HeroSelectionView(
        heroes.map {
            HeroSelectionViewModel(
                it::class.java.name,
                it.name, it.description, it.heroSprite, it.army,
                it.spell.title, it.spell.description, it.spell.cooldown
            )
        },
        heroSelection.selected,
        stage = stage
    )
    private val controller = HeroSelectionController(
        heroSelectionView,
        heroSelection,
        GameStateManager::goBack
    ) {
        GameStateManager.replace(createLobbyState(heroSelection.selectedHero))
    }

    override fun update(dt: Float) {
        controller.update(dt)
    }

    override fun render(sb: SpriteBatch) {
        controller.render(sb)
    }

    override fun dispose() {
        controller.dispose()
    }
}
