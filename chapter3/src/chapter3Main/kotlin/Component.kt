package chapter3

import kotlinx.cinterop.CPointer
import platform.darwin.UInt8Var

open class Component(private val owner: Actor, var updateOrder: Int = 100) {

    init {
        owner.addComponent(this)
    }

    open fun update(deltaTime: Float) = Unit

    open fun processInput(keyState: CPointer<UInt8Var>) = Unit

    open fun dispose() {
        owner.removeComponent(this)
    }
}
