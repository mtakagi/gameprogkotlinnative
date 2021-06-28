package chapter2

import kotlinx.cinterop.*
import sdl.*

open class SpriteComponent(val owner: Actor, var drawOrder: Int = 100) : Component(owner) {

    private var texture: CPointer<SDL_Texture>? = null
    private var textureWidth: Int = 0
    private var textureHeight: Int = 0

    init {
        owner.game.addSprite(this)
    }

    open fun draw(renderer: CPointer<SDL_Renderer>?) {
        memScoped {
            val r = alloc<SDL_Rect>().apply {
                w = (textureWidth.toFloat() * owner.scale).toInt()
                h = (textureHeight.toFloat() * owner.scale).toInt()
                x = (owner.position.x - w / 2).toInt()
                y = (owner.position.y - h / 2).toInt()
            }

            SDL_RenderCopyEx(renderer, texture, null, r.ptr.reinterpret(), -owner.rotation.toDegree().toDouble(), null, SDL_FLIP_NONE)
        }
    }

    fun setTexture(tex: CPointer<SDL_Texture>?) {
        texture = tex
        memScoped {
            val width = alloc<IntVar>()
            val height = alloc<IntVar>()
            SDL_QueryTexture(texture, null, null, width.ptr.reinterpret(), height.ptr.reinterpret())
            textureWidth = width.value
            textureHeight = height.value
        }
    }

    override fun dispose() {
        super.dispose()
        owner.game.removeSprite(this)
    }
}
