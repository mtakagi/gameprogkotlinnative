package chapter3

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.get
import platform.darwin.UInt8Var

class InputComponent(owner: Actor) : MoveComponent(owner) {
    var maxForwardSpeed : Float = 0f
    var maxAngurarSpeed : Float = 0f
    var forwardKey : Int = 0
    var backKey : Int = 0
    var clockwiseKey : Int = 0
    var counterClockwiseKey : Int = 0

    override fun processInput(keyState: CPointer<UInt8Var>) {
        var forwardSpeed = 0f
        if (keyState[forwardKey].toInt() != 0) {
            forwardSpeed += maxForwardSpeed
        }
        if (keyState[backKey].toInt() != 0) {
            forwardSpeed -= maxForwardSpeed
        }
        this.forwardSpeed = forwardSpeed

        var angularSpeed = 0f
        if (keyState[clockwiseKey].toInt() != 0) {
            angularSpeed += maxAngurarSpeed
        }
        if (keyState[counterClockwiseKey].toInt() != 0) {
            angularSpeed -= maxAngurarSpeed
        }
        this.angularSpeed = angularSpeed
    }
}