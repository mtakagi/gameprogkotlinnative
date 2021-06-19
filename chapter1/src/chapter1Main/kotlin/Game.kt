package chapter1

import kotlinx.cinterop.*
import sdl.*

class Game {

    private val thickness = 15
    private val paddleHeight = 100f
    private var isRunning: Boolean = true
    private var ticksCount: UInt = 0u
    private var paddleDir: Int = 0
    private var window: CPointer<SDL_Window>? = null
    private var renderer: CPointer<SDL_Renderer>? = null
    private var paddlePos: Vector2 = Vector2(0f, 0f)
    private var ballPos: Vector2 = Vector2(0f, 0f)
    private var ballVel: Vector2 = Vector2(0f, 0f)

    fun initialize(): Boolean {
        val result = SDL_Init(SDL_INIT_VIDEO)
        if (result != 0) {
            throw Error("Unable to initialize SDL: ${SDL_GetError()}")
        }

        window = SDL_CreateWindow(
            "Game Programming in Kotlin/Native (Chapter 1)", // Window title
            100,	// Top left x-coordinate of window
            100,	// Top left y-coordinate of window
            1024,	// Width of window
            768,	// Height of window
            SDL_WINDOW_SHOWN or SDL_WINDOW_ALLOW_HIGHDPI // Flags (0 for no flags set)
        )

        if (window == null) {
            throw Error("Failed to create window: ${SDL_GetError()}")
        }

        renderer = SDL_CreateRenderer(
            window, // Window to create renderer for
            -1, // Usually -1
            SDL_RENDERER_ACCELERATED or SDL_RENDERER_PRESENTVSYNC
        )

        if (renderer == null) {
            throw Error("Failed to create renderer: ${SDL_GetError()}")
        }

        paddlePos.x = 10.0f
        paddlePos.y = 768.0f / 2.0f
        ballPos.x = 1024.0f / 2.0f
        ballPos.y = 768.0f / 2.0f
        ballVel.x = -200.0f
        ballVel.y = 235.0f

        return true
    }

    fun runloop() {
        while (isRunning) {
            processInput()
            updateGame()
            generateOutput()
        }
    }

    private fun processInput() {
        memScoped {
            val event = alloc<SDL_Event>()
            while (SDL_PollEvent(event.ptr.reinterpret()) != 0) {
                when (event.type) {
                    SDL_QUIT -> isRunning = false
                }
            }
        }

        val state = SDL_GetKeyboardState(null) ?: return
        if (state[SDL_SCANCODE_ESCAPE.toInt()].toInt() != 0) {
            isRunning = false
        }
        paddleDir = 0
        if (state[SDL_SCANCODE_W.toInt()].toInt() != 0 || state[SDL_SCANCODE_UP.toInt()].toInt() != 0) {
            paddleDir -= 1
        }
        if (state[SDL_SCANCODE_S.toInt()].toInt() != 0 || state[SDL_SCANCODE_DOWN.toInt()].toInt() != 0) {
            paddleDir += 1
        }
    }

    private fun updateGame() {
        // Wait until 16ms has elapsed since last frame
        while (!SDL_TICKS_PASSED(SDL_GetTicks().toInt(), ticksCount.toInt() + 16)) Unit

        // Delta time is the difference in ticks from last frame
        // (converted to seconds)
        var deltaTime = (SDL_GetTicks() - ticksCount).toFloat() / 1000.0f

        // Clamp maximum delta time value
        if (deltaTime > 0.05f) {
            deltaTime = 0.05f
        }

        // Update tick counts (for next frame)
        ticksCount = SDL_GetTicks()

        // Update paddle position based on direction
        if (paddleDir != 0) {
            paddlePos.y += paddleDir * 300.0f * deltaTime
            // Make sure paddle doesn't move off screen!
            if (paddlePos.y < (paddleHeight / 2.0f + thickness)) {
                paddlePos.y = paddleHeight / 2.0f + thickness
            } else if (paddlePos.y > (768.0f - paddleHeight / 2.0f - thickness)) {
                paddlePos.y = 768.0f - paddleHeight / 2.0f - thickness
            }
        }

        // Update ball position based on ball velocity9090
        ballPos.x += ballVel.x * deltaTime
        ballPos.y += ballVel.y * deltaTime

        // Bounce if needed
        // Did we intersect with the paddle?
        var diff = paddlePos.y - ballPos.y
        // Take absolute value of difference
        diff = if (diff > 0.0f) diff else -diff
        if (
            // Our y-difference is small enough
            diff <= paddleHeight / 2.0f &&
            // We are in the correct x-position
            ballPos.x <= 25.0f && ballPos.x >= 20.0f &&
            // The ball is moving to the left
            ballVel.x < 0.0f
        ) {
            ballVel.x *= -1.0f
        }
        // Did the ball go off the screen? (if so, end game)
        else if (ballPos.x <= 0.0f) {
            isRunning = false
        }
        // Did the ball collide with the right wall?
        else if (ballPos.x >= (1024.0f - thickness) && ballVel.x > 0.0f) {
            ballVel.x *= -1.0f
        }

        // Did the ball collide with the top wall?
        if (ballPos.y <= thickness && ballVel.y < 0.0f) {
            ballVel.y *= -1
        }
        // Did the ball collide with the bottom wall?
        else if (ballPos.y >= (768 - thickness) &&
            ballVel.y > 0.0f
        ) {
            ballVel.y *= -1
        }
    }

    private fun generateOutput() {
        // Set draw color to blue
        SDL_SetRenderDrawColor(
            renderer,
            0, // R
            0, // G 
            255,	// B
            255 // A
        )

        // Clear back buffer
        SDL_RenderClear(renderer)

        // Draw walls
        SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255)

        memScoped {
            val wall = alloc<SDL_Rect>().apply {
                x = 0
                y = 0
                w = 1024
                h = thickness
            }

            SDL_RenderFillRect(renderer, wall.ptr.reinterpret())

            wall.y = 768 - thickness

            SDL_RenderFillRect(renderer, wall.ptr.reinterpret())

            wall.x = 1024 - thickness
            wall.y = 0
            wall.w = thickness
            wall.h = 1024

            SDL_RenderFillRect(renderer, wall.ptr.reinterpret())

            val paddle = alloc<SDL_Rect>().apply {
                x = paddlePos.x.toInt()
                y = (paddlePos.y - paddleHeight / 2).toInt()
                w = thickness
                h = paddleHeight.toInt()
            }

            SDL_RenderFillRect(renderer, paddle.ptr.reinterpret())

            val ball = alloc<SDL_Rect>().apply {
                x = (ballPos.x - thickness / 2).toInt()
                y = (ballPos.y - thickness / 2).toInt()
                w = thickness
                h = thickness
            }

            SDL_RenderFillRect(renderer, ball.ptr.reinterpret())
        }

        // Swap front buffer and back buffer
        SDL_RenderPresent(renderer)
    }

    fun shutdown() {
        SDL_DestroyRenderer(renderer)
        SDL_DestroyWindow(window)
        SDL_Quit()
    }

    private inline fun SDL_TICKS_PASSED(a: Int, b: Int): Boolean = (b - a) <= 0
}
