package chapter3

import kotlinx.cinterop.*
import platform.darwin.UInt8Var
import sdl.*

class Ship(game: Game) : Actor(game) {

    private var laserCoolDown = 0.0f

    init {
        SpriteComponent(this, 150).apply {
            setTexture(game.getTexture("Assets/Ship.png"))
        }

        InputComponent(this).apply {
            forwardKey = SDL_SCANCODE_W.toInt()
            backKey = SDL_SCANCODE_S.toInt()
            clockwiseKey = SDL_SCANCODE_A.toInt()
            counterClockwiseKey = SDL_SCANCODE_D.toInt()
            maxForwardSpeed = 300f
            maxAngurarSpeed = 3.14f * 2f
        }
    }

    override fun updateActor(deltaTime: Float) {
        laserCoolDown -= deltaTime
    }

    override fun actorInput(input: CPointer<UInt8Var>) {
        if (input[SDL_SCANCODE_SPACE.toInt()].toInt() != 0 && laserCoolDown <= 0.0f) {
            val laser = Laser(game)
            laser.position = position
            laser.rotation = rotation
            laserCoolDown = 0.5f
        }
    }
}
