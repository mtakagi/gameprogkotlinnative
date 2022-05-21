package chapter3

import kotlinx.cinterop.CPointer
import platform.darwin.UInt8Var
import kotlin.collections.*
import kotlin.math.cos
import kotlin.math.sin

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

    open fun updateActor(deltaTime: Float) = Unit

    fun processInput(keyState: CPointer<UInt8Var>) {
        if (state != State.Active) return

        for (compnent in components) {
            compnent.processInput(keyState)
        }

        actorInput(keyState)
    }

    open fun actorInput(input: CPointer<UInt8Var>) = Unit

    fun addComponent(component: Component) {
        components.add(component)
        components.sortBy { it.updateOrder }
    }

    fun removeComponent(component: Component) {
        components.remove(component)
    }

    open fun dispose() {
        game.removeActor(this)
        val array = components.toTypedArray()
        for (components in array) {
            components.dispose()
        }
        components.clear()
    }

    fun getForward() : Vector2 = Vector2(cos(rotation), -sin(rotation))
}
