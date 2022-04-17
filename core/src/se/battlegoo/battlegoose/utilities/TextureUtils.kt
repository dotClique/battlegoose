package se.battlegoo.battlegoose.utilities

import com.badlogic.gdx.graphics.Texture

/**
 * Scale the texture-dimensions to fit within the given targetWidth and targetHeight.
 * Returns a Quad-value with a=Width and b=Height within the given constraints, while maintaining
 * the original aspect ratio. Also returns margins: c=horizontalMargin and d=verticalMargin, which
 * may be used to center the texture horizontally, vertically or both. To center within targetWidth,
 * add horizontalMargin to the x-position. Likewise for targetHeight/verticalMargin.
 */
fun fitScale(texture: Texture, targetWidth: Float, targetHeight: Float): Quad<Float, Float, Float, Float> {
    val targetRatio = targetWidth / targetHeight
    val originRatio = texture.width.toFloat() / texture.height.toFloat()

    return when {
        // If width/height is larger for target than origin, scale width
        targetRatio > originRatio -> {
            val newWidth = targetHeight / texture.height * texture.width
            Quad(newWidth, targetHeight, (targetWidth - newWidth) / 2, 0f)
        }
        // If width/height is lower for target than origin, scale height
        targetRatio < originRatio -> {
            val newHeight = targetWidth / texture.width * texture.height
            Quad(targetWidth, newHeight, 0f, (targetHeight - newHeight) / 2)
        }
        else -> Quad(targetWidth, targetHeight, 0f, 0f)
    }
}
