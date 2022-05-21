package chapter3

class Asteroid(game : Game) : Actor(game) {
    val circle : CircleComponent

    init {
        val randomPos = Random.getVector(Vector2.zero, Vector2(1024f, 768f))
        position = randomPos
        rotation = Random.getFloatRange(0f, 3.14f * 2)

        val sprite = SpriteComponent(this)
        sprite.setTexture(game.getTexture("Assets/Asteroid.png"))

        val move = MoveComponent(this)
        move.forwardSpeed = 150f

        circle = CircleComponent(this)
        circle.radius = 40f

        game.addAsteroid(this)
    }

    override fun dispose() {
        super.dispose()
        game.removeAsteroid(this)
    }
}