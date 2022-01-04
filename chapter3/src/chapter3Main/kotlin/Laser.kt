package chapter3

class Laser(game: Game) : Actor(game) {

    private var deathTimer = 1.0f
    private val circleComponent : CircleComponent

    init {
        val spriteComponent = SpriteComponent(this)
        spriteComponent.setTexture(game.getTexture("Assets/Laser.png"))

        val moveComponent = MoveComponent(this)
        moveComponent.forwardSpeed = 800.0f

        circleComponent = CircleComponent(this)
    }

    override fun updateActor(deltaTime: Float) {
        deathTimer -= deltaTime
        if (deathTimer <= 0.0f)
        {
            state = State.Dead
        } else {
            for(asteroid in game.asteroids) {
                if (intersect(circleComponent, asteroid.circle)) {
                    state = State.Dead
                    asteroid.state = State.Dead
                    break
                }
            }
        }
    }
}