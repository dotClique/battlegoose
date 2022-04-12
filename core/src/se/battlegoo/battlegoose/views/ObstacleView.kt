package se.battlegoo.battlegoose.views

import se.battlegoo.battlegoose.models.Obstacle

open class ObstacleView(type: Obstacle) : SpriteViewBase(
    when (type) {
        Obstacle.BUSH -> "bush.png"
        Obstacle.ROCK -> "rock.png"
        Obstacle.BEACH_BALL -> "beachBall.png"
    }
)
