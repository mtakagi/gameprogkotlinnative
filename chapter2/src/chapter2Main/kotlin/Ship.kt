package chapter2

import kotlinx.cinterop.*
import sdl.*

class Ship(game: Game) : Actor(game) {

    private var rightSpeed = 0.0f
    private var downSpeed = 0.0f

    init {
        val component = AnimSpriteComponent(this)
        component.setAnimationTextures(
            listOf(
                game.getTexture("Assets/Ship01.png"),
                game.getTexture("Assets/Ship02.png"),
                game.getTexture("Assets/Ship03.png"),
                game.getTexture("Assets/Ship04.png")
            )
        )
    }

    override fun updateActor(deltaTime: Float) {
        super.updateActor(deltaTime)
        val pos: Vector2 = position
        pos.x += rightSpeed * deltaTime
        pos.y += downSpeed * deltaTime
        // Restrict position to left half of screen
        // Restrict position to left half of screen
        if (pos.x < 25.0f) {
            pos.x = 25.0f
        } else if (pos.x > 500.0f) {
            pos.x = 500.0f
        }
        if (pos.y < 25.0f) {
            pos.y = 25.0f
        } else if (pos.y > 743.0f) {
            pos.y = 743.0f
        }
        position = pos
    }

    fun processKeyboard(state: CPointer<Uint8Var>?) {
        state ?: return
        rightSpeed = 0.0f
        downSpeed = 0.0f
        // right/left
        if (state[SDL_SCANCODE_D.toInt()].toInt() != 0) {
            rightSpeed += 250.0f
        }
        if (state[SDL_SCANCODE_A.toInt()].toInt() != 0) {
            rightSpeed -= 250.0f
        }
        // up/down
        if (state[SDL_SCANCODE_S.toInt()].toInt() != 0) {
            downSpeed += 300.0f
        }
        if (state[SDL_SCANCODE_W.toInt()].toInt() != 0) {
            downSpeed -= 300.0f
        }
    }
}
