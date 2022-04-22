package se.battlegoo.battlegoose.utilities

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import se.battlegoo.battlegoose.ScreenVector

/**
 * Scale the texture-dimensions to fit within the given targetWidth and targetHeight.
 * Returns a FitDimensions-value with a ScreenVector for size and a ScreenVector for margins
 * The returned size is within the given constraints, while maintaining the original aspect ratio.
 * The returned margins may be used to center the texture horizontally, vertically or both.
 * To center within targetWidth, add horizontalMargin to the x-position.
 * Likewise for targetHeight/verticalMargin.
 */
fun fitScale(texture: Texture, targetSize: ScreenVector):
    FitDimensions {

    val targetRatio = targetSize.x / targetSize.y
    val originRatio = texture.width.toFloat() / texture.height.toFloat()

    return when {
        // If width/height is larger for target than origin, scale width
        targetRatio > originRatio -> {
            val newWidth = targetSize.y / texture.height * texture.width
            FitDimensions(
                ScreenVector(newWidth, targetSize.y),
                ScreenVector((targetSize.x - newWidth) / 2, 0f)
            )
        }
        // If width/height is lower for target than origin, scale height
        targetRatio < originRatio -> {
            val newHeight = targetSize.x / texture.width * texture.height
            FitDimensions(
                ScreenVector(targetSize.x, newHeight),
                ScreenVector(0f, (targetSize.y - newHeight) / 2)
            )
        }
        else -> FitDimensions(
            targetSize,
            ScreenVector(0f, 0f)
        )
    }
}

data class FitDimensions(val size: ScreenVector, val margins: ScreenVector)

fun createSolidColorTexture(width: Float, height: Float, color: Color): Texture {
    val pixmap = Pixmap(width.toInt(), height.toInt(), Pixmap.Format.RGBA8888)
    pixmap.setColor(color)
    pixmap.fill()
    return Texture(pixmap)
}

fun createDrawableOfTexture(texture: Texture): Drawable {
    return TextureRegionDrawable(TextureRegion(texture))
}

fun emptyDrawable(): Drawable {
    return createDrawableOfTexture(createSolidColorTexture(0f, 0f, Color.BLACK))
}
