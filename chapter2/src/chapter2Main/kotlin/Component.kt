package chapter2

open class Component constructor(private val owner: Actor, var updateOrder: Int = 100) {

    init {
        owner.addComponent(this)
    }

    open fun update(deltaTime: Float) {
    }

    open fun dispose() {
        owner.removeComponent(this)
    }
}
