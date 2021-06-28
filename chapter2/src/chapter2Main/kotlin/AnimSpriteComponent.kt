package chapter2

import kotlinx.cinterop.*
import sdl.*

class AnimSpriteComponent(owner: Actor, drawOrder: Int = 100) : SpriteComponent(owner, drawOrder) {

    private var animationTextures: List<CPointer<SDL_Texture>?> = listOf()
    private var currentFrame = 0.0f
    private var animationFps = 2.4f

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        if (animationTextures.isNotEmpty()) {
            // Update the current frame based on frame rate
            // and delta time
            currentFrame += animationFps * deltaTime

            // Wrap current frame if needed
            while (currentFrame >= animationTextures.size) {
                currentFrame -= animationTextures.size
            }

            // Set the current texture
            setTexture(animationTextures[currentFrame.toInt()])
        }
    }

    fun setAnimationTextures(textures: List<CPointer<SDL_Texture>?>) {
        animationTextures = textures

        if (animationTextures.isNotEmpty()) {
            currentFrame = 0.0f
            setTexture(animationTextures[0])
        }
    }
}
