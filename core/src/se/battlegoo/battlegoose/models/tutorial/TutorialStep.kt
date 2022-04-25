package se.battlegoo.battlegoose.models.tutorial

data class TutorialStep(
    val tutorialSprite: TutorialSprite,
    val headerText: String,
    val tutorialText: String,
    val extraText: String? = null,
    val first: Boolean = false,
    val last: Boolean = false
)

enum class TutorialSprite {
    INTERNET_CONNECTION,
    MAIN_MENU,
    CREATE_LOBBY,
    JOIN_LOBBY,
    QUICK_JOIN,
    HERO_SELECT,
    LEADERBOARD,
    BATTLE_GENERAL,
    BATTLE_MOVE_AND_ATTACK,
    BATTLE_SPELL
}
