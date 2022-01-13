package chapter2

import kotlinx.cinterop.*
import sdl.*

class BGSpriteComponent(owner: Actor, drawOrder: Int = 10) : SpriteComponent(owner, drawOrder) {

    data class BGTexture(val texture: CPointer<SDL_Texture>, val offset: Vector2)

    val bgTextureList = mutableListOf<BGTexture>()
    var screenSize: Vector2 = Vector2.zero
    var scrollSpeed: Float = 0.0f

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        for (bg in bgTextureList) {
            // Update the x offset
            bg.offset.x += scrollSpeed * deltaTime
            // If this is completely off the screen, reset offset to
            // the right of the last bg texture
            if (bg.offset.x < -screenSize.x) {
                bg.offset.x = (bgTextureList.size - 1) * screenSize.x - 1
            }
        }
    }

    override fun draw(renderer: CPointer<SDL_Renderer>?) {
        for (bg in bgTextureList) {
            memScoped {
                val r = alloc<SDL_Rect>().apply {
                    // Assume screen size dimensions
                    w = screenSize.x.toInt()
                    h = screenSize.y.toInt()
                    // Center the rectangle around the position of the owner
                    x = (owner.position.x - w / 2 + bg.offset.x).toInt()
                    y = (owner.position.y - h / 2 + bg.offset.y).toInt()
                }
                // Draw this background
                SDL_RenderCopy(renderer, bg.texture, null, r.ptr.reinterpret())
            }
        }
    }

    fun setBGTextures(textures: List<CPointer<SDL_Texture>?>) {
        for ((count, tex) in textures.withIndex()) {
            tex ?: continue
            val offset = Vector2(x = count * screenSize.x, 0f)
            val temp = BGTexture(texture = tex, offset = offset)
            bgTextureList.add(temp)
        }
    }
}
