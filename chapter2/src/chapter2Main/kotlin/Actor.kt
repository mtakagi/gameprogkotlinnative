package chapter2

import kotlin.collections.*

open class Actor(val game: Game) {

    enum class State {
        Active,
        Paused,
        Dead
    }

    var position: Vector2 = Vector2.zero
    var scale: Float = 1.0f
    var rotation: Float = 0.0f
    var state: State = State.Active
    private var components = mutableListOf<Component>()

    init {
        game.addActor(this)
    }

    fun update(deltaTime: Float) {
        if (state == State.Active) {
            updateComponents(deltaTime)
            updateActor(deltaTime)
        }
    }

    private fun updateComponents(deltaTime: Float) {
        for (component in components) {
            component.update(deltaTime)
        }
    }

    open fun updateActor(deltaTime: Float) {
    }

    fun addComponent(component: Component) {
        components.add(component)
        components.sortBy { it.updateOrder }
    }

    fun removeComponent(component: Component) {
        components.remove(component)
    }

    fun dispose() {
        game.removeActor(this)
        for (component in components) {
            component.dispose()
        }
    }
}
