package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.controllers.TutorialController
import se.battlegoo.battlegoose.models.tutorial.Tutorial
import se.battlegoo.battlegoose.models.tutorial.TutorialSprite
import se.battlegoo.battlegoose.models.tutorial.TutorialStep
import se.battlegoo.battlegoose.views.TutorialView

class TutorialState : GameState() {

    private val steps: List<TutorialStep> = listOf(
        TutorialStep(
            TutorialSprite.MAIN_MENU,
            "Main Menu",
            "From the main menu you can: " +
                "\n\na) Create a lobby" +
                "\nCreate a private game lobby to which you can invite a friend to play with you" +
                "\n\nb) Join a lobby" +
                "\nIf a friend sends you a lobby-id to a private lobby, you may enter it here to " +
                "join them",
            "\n\nc) Quick join" +
                "\nAre all you friends busy? Want a new challenge? Then join an internet " +
                "stranger to play with online" +
                "\n\nd) Access the leaderboard" +
                "\nWant to know how you're doing? Go to the leaderboard to see the " +
                "top 10 players globally" +
                "\n\n",
        ),
        TutorialStep(
            TutorialSprite.CREATE_LOBBY,
            "Create Lobby",
            "When the text (a) says \"Waiting for opponent\", your lobby is available. " +
                "You may now send the lobby id (b) to a friend, and ask them to enter it on the" +
                "\"Join lobby\"-screen, which is available from the main menu"
        ),
        TutorialStep(
            TutorialSprite.HERO_SELECT,
            "Hero Selection",
            "Here you select which hero to play with in this battle. " +
                "Each hero has different spells and units which affects the gameplay. " +
                "Try some out, and find your favorite!"
        )
    )
    private val tutorial: Tutorial = Tutorial(steps)
    private val tutorialView = TutorialView(stage)
    private var controller: TutorialController = TutorialController(
        tutorialView, tutorial, GameStateManager::goBack
    )

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
