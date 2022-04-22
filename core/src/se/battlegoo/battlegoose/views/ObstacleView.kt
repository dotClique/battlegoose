package se.battlegoo.battlegoose.views

import se.battlegoo.battlegoose.models.Obstacle

open class ObstacleView(type: Obstacle) : SpriteViewBase(
    when (type) {
        Obstacle.BUSH -> "obstacles/bush.png"
        Obstacle.ROCK -> "obstacles/rock.png"
        Obstacle.BEACH_BALL -> "obstacles/beachBall.png"
    }
)
