package chapter2

import kotlinx.cinterop.*
import sdl.*

class Game {

    private var isRunning: Boolean = true
    private var ticksCount: UInt = 0u
    private var window: CPointer<SDL_Window>? = null
    private var renderer: CPointer<SDL_Renderer>? = null
    private var ship: Ship? = null
    private var actorList = mutableListOf<Actor>()
    private var pendingActorList = mutableListOf<Actor>()
    private var spriteList = mutableListOf<SpriteComponent>()
    private var textureMap = mutableMapOf<String, CPointer<SDL_Texture>>()
    private var updateActors = false

    fun initialize(): Boolean {
        val result = SDL_Init(SDL_INIT_VIDEO.toUInt() or SDL_INIT_AUDIO.toUInt())
        if (result != 0) {
            throw Error("Unable to initialize SDL: ${SDL_GetError()}")
        }

        window = SDL_CreateWindow(
            "Game Programming in Kotlin/Native (Chapter 2)", // Window title
            100,	// Top left x-coordinate of window
            100,	// Top left y-coordinate of window
            1024,	// Width of window
            768,	// Height of window
            SDL_WINDOW_SHOWN // Flags (0 for no flags set)
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

        if (IMG_Init(IMG_INIT_PNG.toInt()) == 0) {
            throw Error("Unable to initialize SDL_image: ${SDL_GetError()}")
        }

        loadData()

        ticksCount = SDL_GetTicks()

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

        val state = SDL_GetKeyboardState(null)
        state ?: return
        if (state[SDL_SCANCODE_ESCAPE.toInt()].toInt() != 0) {
            isRunning = false
        }

        ship?.processKeyboard(state)
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

        updateActors = true

        for (actor in actorList) {
            actor.update(deltaTime)
        }

        updateActors = false

        actorList.addAll(pendingActorList)
        pendingActorList.clear()

        val deadActor = mutableListOf<Actor>()

        for (actor in actorList) {
            if (actor.state == Actor.State.Dead) {
                deadActor.add(actor)
            }
        }

        deadActor.forEach {
            it.dispose()
        }
    }

    private fun generateOutput() {
        SDL_SetRenderDrawColor(renderer, 0, 0, 0, 255)
        SDL_RenderClear(renderer)

        spriteList.forEach { it.draw(renderer) }

        // Swap front buffer and back buffer
        SDL_RenderPresent(renderer)
    }

    fun shutdown() {
        unloadData()
        IMG_Quit()
        SDL_DestroyRenderer(renderer)
        SDL_DestroyWindow(window)
        SDL_Quit()
    }

    private fun loadData() {
        ship = Ship(this).apply {
            position = Vector2(100f, 384f)
            scale = 1.5f
        }

        val actor = Actor(this).apply {
            position = Vector2(512f, 384f)
        }

        BGSpriteComponent(actor).apply {
            screenSize = Vector2(1024f, 768f)
            setBGTextures(
                listOf(
                    getTexture("Assets/Farback01.png"),
                    getTexture("Assets/Farback02.png")
                )
            )
            scrollSpeed = -100f
        }
        BGSpriteComponent(actor, 50).apply {
            screenSize = Vector2(1024f, 768f)
            setBGTextures(
                listOf(
                    getTexture("Assets/Stars.png"),
                    getTexture("Assets/Stars.png")
                )
            )
            scrollSpeed = -200f
        }
    }

    private fun unloadData() {
        actorList.forEach {
            it.dispose()
        }
        textureMap.forEach {
            SDL_DestroyTexture(it.value)
        }
        textureMap.clear()
    }

    fun addActor(actor: Actor) {
        if (updateActors) {
            pendingActorList
        } else {
            actorList
        }.add(actor)
    }

    fun removeActor(actor: Actor) {
        pendingActorList.remove(actor)
        actorList.remove(actor)
    }

    fun addSprite(sprite: SpriteComponent) {
        spriteList.add(sprite)
        spriteList.sortBy { it.drawOrder }
    }

    fun removeSprite(sprite: SpriteComponent) {
        spriteList.remove(sprite)
    }

    fun getTexture(path: String): CPointer<SDL_Texture> {
        var texture = textureMap[path]
        if (texture != null) {
            return texture
        } else {
            val surface = IMG_Load(path) ?: throw Error("Failed to load texture file $path")

            texture = SDL_CreateTextureFromSurface(renderer, surface)
            SDL_FreeSurface(surface)
            if (texture == null) {
                throw Error("Failed to convert surface to texture for $path")
            }
            textureMap[path] = texture
        }
        return texture
    }

    private inline fun SDL_TICKS_PASSED(a: Int, b: Int): Boolean = (b - a) <= 0
}
