package se.battlegoo.battlegoose.views

import se.battlegoo.battlegoose.models.Obstacle
import se.battlegoo.battlegoose.utils.TextureAsset
import se.battlegoo.battlegoose.views.utils.SpriteViewBase

open class ObstacleView(type: Obstacle) : SpriteViewBase(
    when (type) {
        Obstacle.BUSH -> TextureAsset.OBSTACLE_BUSH
        Obstacle.ROCK -> TextureAsset.OBSTACLE_ROCK
        Obstacle.BEACH_BALL -> TextureAsset.OBSTACLE_BEACH_BALL
    }
)
