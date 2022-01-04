package chapter3

open class MoveComponent(private val owner: Actor) : Component(owner) {

    var angularSpeed = 0f
    var forwardSpeed = 0f

    init {
        updateOrder = 10
    }

    override fun update(deltaTime: Float) {
        if (!angularSpeed.nearZero())
        {
            var rot = owner.rotation
            rot += angularSpeed * deltaTime
            owner.rotation = rot
        }

        if (!forwardSpeed.nearZero())
        {
            var pos = owner.position
            pos += owner.getForward() * forwardSpeed * deltaTime

            if (pos.x < 0.0f) { pos.x = 1022.0f; }
            else if (pos.x > 1024.0f) { pos.x = 2.0f; }

            if (pos.y < 0.0f) { pos.y = 766.0f; }
            else if (pos.y > 768.0f) { pos.y = 2.0f; }

            owner.position = pos
        }
    }
}