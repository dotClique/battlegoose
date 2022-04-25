package se.battlegoo.battlegoose.views.heroes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.datamodels.ScreenVector
import se.battlegoo.battlegoose.models.heroes.HeroSprite
import se.battlegoo.battlegoose.models.units.DelinquentDuck
import se.battlegoo.battlegoose.models.units.GuardGoose
import se.battlegoo.battlegoose.models.units.PrivatePenguin
import se.battlegoo.battlegoose.models.units.SpitfireSeagull
import se.battlegoo.battlegoose.models.units.UnitModel
import se.battlegoo.battlegoose.utils.Modal
import se.battlegoo.battlegoose.utils.ModalType
import se.battlegoo.battlegoose.views.utils.Fonts
import se.battlegoo.battlegoose.views.utils.Skins
import kotlin.reflect.KClass

class HeroDetailsView(
    heroDetailsViewModel: HeroDetailsViewModel,
    val stage: Stage,
    private val onExit: (() -> Unit)? = null,
) {

    companion object {
        // Relative values in percentage (0f - 1f) to place elements on the background
        private const val TABLE_COLUMN_SPELL_TITLE_WIDTH = 0.3f

        // Color selection
        private val COLOR_FONT_HEADER = Color.RED
    }

    //    private val backgroundTexture: Texture = Texture("heroSelection/heroDetails.png")
    private val heroTexture: Texture = Texture(
        when (heroDetailsViewModel.heroSprite) {
            HeroSprite.SERGEANT_SWAN -> "heroes/sergeantSwan.png"
            HeroSprite.MAJOR_MALLARD -> "heroes/majorMallard.png"
            HeroSprite.ADMIRAL_ALBATROSS -> "heroes/admiralAlbatross.png"
        }
    )

    private val heroSprite: Image = Image(heroTexture)

    private val mainSkin: Skin = Skin(Gdx.files.internal(Skins.STAR_SOLDIER.filepath))
    private val headerLabelStyle: Label.LabelStyle = Label.LabelStyle(
        mainSkin.getFont(Fonts.STAR_SOLDIER.identifier), Color.WHITE
    )
    private val textSkin: Skin = Skin(Gdx.files.internal(Skins.PLAIN_JAMES.filepath))
    private val bodyLabelStyle: Label.LabelStyle = Label.LabelStyle(
        textSkin.getFont(Fonts.PLAIN_JAMES.identifier), Color.WHITE
    )

    private val textTable: Table = Table(mainSkin)

    private val descriptionLabel = Label(heroDetailsViewModel.description, bodyLabelStyle)
    private val spellHeaderLabel = Label("Spell:", headerLabelStyle)
    private val spellNameLabel = Label(heroDetailsViewModel.spellName, bodyLabelStyle)
    private val spellDescriptionLabel = Label(
        "${heroDetailsViewModel.spellDescription}\n" +
            "${heroDetailsViewModel.spellCooldown} turns cooldown.",
        bodyLabelStyle
    )

    private val armyLabel = Label(
        "Army: " + heroDetailsViewModel.army.let { army ->
            army.associateWith { unitClass -> army.count(unitClass::equals) }
        }.toList().joinToString { (unitClass, count) ->
            when (unitClass) {
                DelinquentDuck::class -> "Delinquent Duck"
                GuardGoose::class -> "Guard Goose"
                PrivatePenguin::class -> "Private Penguin"
                SpitfireSeagull::class -> "Spitfire Seagull"
                else -> throw IllegalArgumentException("Unknown unit type")
            } + if (count > 1) " (x$count)" else ""
        },
        bodyLabelStyle
    )

    private var modal: Modal

    init {

        val textBoxSize = ScreenVector(
            Game.WIDTH / 2.1f,
            Game.HEIGHT * 0.8f
        )

        textTable.setSize(textBoxSize.x, textBoxSize.y)
        textTable.left().top() // Align content from top left

        spellHeaderLabel.color = COLOR_FONT_HEADER
        descriptionLabel.wrap = true
        armyLabel.wrap = true
        spellHeaderLabel.wrap = true
        spellNameLabel.wrap = true
        spellDescriptionLabel.wrap = true

        armyLabel.setFontScale(1.2f)
        descriptionLabel.setFontScale(2f)
        spellNameLabel.setFontScale(2f)
        spellDescriptionLabel.setFontScale(2f)

        // Set default values for table cells
        textTable.defaults().fill().left().colspan(2)
        // Add and position all text elements
        textTable.add(heroSprite).width(textBoxSize.y / 3).height(textBoxSize.y / 3)
            .colspan(2)
            .center()
        textTable.row()
        textTable.add(armyLabel).width(textBoxSize.x)
        textTable.row()
        textTable.add(descriptionLabel).width(textBoxSize.x)
        textTable.row().spaceTop(Game.HEIGHT / 20).colspan(1)
        textTable.add(spellHeaderLabel).width(textBoxSize.x * TABLE_COLUMN_SPELL_TITLE_WIDTH)
        textTable.add(spellNameLabel).expandX()
        textTable.row().width(textBoxSize.x)
        textTable.add(spellDescriptionLabel)

        modal = Modal(
            heroDetailsViewModel.name,
            null,
            ModalType.Info {
                heroTexture.dispose()
                mainSkin.dispose()
                textSkin.dispose()
                onExit?.invoke()
            },
            stage,
            contentActors = listOf(textTable),
            prefHeight = Game.HEIGHT * 0.9f
        )
    }

    fun show() {
        modal.show()
    }
}

data class HeroDetailsViewModel(
    val id: String,
    val name: String,
    val description: String,
    val heroSprite: HeroSprite,
    val army: List<KClass<out UnitModel>>,
    val spellName: String,
    val spellDescription: String,
    val spellCooldown: Int
)
