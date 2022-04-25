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
            TutorialSprite.INTERNET_CONNECTION,
            "Internet Connection",
            "Note that BattleGoose is a multiplayer-only game, and requires an " +
                "active internet connection at all times."
        ),
        TutorialStep(
            TutorialSprite.MAIN_MENU,
            "Main Menu",
            "1) Create a private lobby\n" +
                "Create a private lobby to which you can invite a friend to play with you.\n" +
                "\n" +
                "2) Join a lobby\n" +
                "If a friend sends you a lobby-id to a private lobby, you may enter it here to " +
                "join them.\n" +
                "\n" +
                "3) Quick join\n" +
                "Are all you friends busy? Want a new challenge? Then join an internet stranger " +
                "to play with online.",
            "4) Access the leaderboard\n" +
                "Want to know how you're doing? Go to the leaderboard to see the top 10 players " +
                "globally.\n" +
                "\n" +
                "5) How to play\n" +
                "To open the tutorial, press the \"How to play\"-button.\n" +
                "\n" +
                "6) Change username\n" +
                "To change you username shown in the leaderboard, press the text field in the " +
                "upper right corner.\n"
        ),
        TutorialStep(
            TutorialSprite.HERO_SELECT,
            "Hero Selection",
            "Before you can create or enter a lobby you have to select a hero to " +
                "play with. Simply press on one of the heroes to select it. Which hero you " +
                "select affects which units you will play with, and which spell you have " +
                "available in battle. To read more about each hero, press the \"Info\"-button."
        ),
        TutorialStep(
            TutorialSprite.CREATE_LOBBY,
            "Create a Lobby",
            "When the text at the top says \"Waiting for opponent\", your lobby is " +
                "available to join. You may now send the lobby id to a friend, and ask them to " +
                "enter it on the \"Join lobby\"-screen, which is available from the main menu. " +
                "When they have done so, the \"Start battle\"-button will appear in the lower " +
                "right corner. Press this button to start the game."
        ),
        TutorialStep(
            TutorialSprite.JOIN_LOBBY,
            "Join a Lobby",
            "To play with a friend, one of you have to create a private lobby through " +
                "the \"Create lobby\"-screen. That user then becomes the host. The other user " +
                "then enters the lobby id in the text field and press the \"Join\"-button. " +
                "The game will begin once the host presses the \"Start battle\"-button on " +
                "their screen."
        ),
        TutorialStep(
            TutorialSprite.QUICK_JOIN,
            "Quick Join",
            "To pair up with a random opponent online, simply wait until one becomes " +
                "available. When an opponent is found, you will automatically start to battle."
        ),
        TutorialStep(
            TutorialSprite.LEADERBOARD,
            "Leaderboard",
            "On the leaderboard you can see the score of the top 10 players globally. " +
                "Winning a battle earns you 1 point, losing a battle retracts 1 point, and " +
                "neither user will receive or loose points if it’s a tie."
        ),
        TutorialStep(
            TutorialSprite.BATTLE_GENERAL,
            "Battle: General",
            "When in a battle you will see your units start on the left side and " +
                "always facing right, while your opponents units starts on the right side and " +
                "always face left. The dagger-icon on the hero-icon shows whose turn it is.\n" +
                "\n" +
                "There may be several obstacles which units have to move around. Beware that " +
                "units may attack through obstacles if they have sufficient range.\n" +
                "\n" +
                "You can only move units, cast a spell or surrender during your own turn. " +
                "Every action (move unit or cast spell) costs 1 action point, which each hero " +
                "has 1 of per turn. Some spells may modify the available action points."
        ),
        TutorialStep(
            TutorialSprite.BATTLE_MOVE_AND_ATTACK,
            "Battle: Move and Attack",
            "To move a unit, press on it to select it. The units stats will then be " +
                "shown in the scroll on the left side of the screen.\n" +
                "\n" +
                "The orange highlight shows the selected unit, the blue highlights show where " +
                "that unit can move to, and red highlights show enemy units the unit can attack. " +
                "\n" +
                "\n" +
                "A unit will not move to attack, so a unit can only attack units within their " +
                "attack range from where they currently stand.\n"
        ),
        TutorialStep(
            TutorialSprite.BATTLE_SPELL,
            "Battle: Spell Casting",
            "During your turn you can cast a hero spell, which is different for each " +
                "hero. Press the \"Cast spell\"-button to open the spell-info, and then confirm " +
                "to cast it. Casting a spell costs one action point."
        )
    )

    private var controller: TutorialController = TutorialController(
        TutorialView(stage), Tutorial(steps), GameStateManager::goBack
    )

    override fun update(dt: Float) = controller.update(dt)

    override fun render(sb: SpriteBatch) = controller.render(sb)

    override fun dispose() = controller.dispose()
}
