package chapter3

data class Vector2(var x: Float, var y: Float) {
    companion object {
        val zero = Vector2(0f, 0f)
    }

    fun lengthSq() : Float = x * x + y * y

    operator fun plus(other : Vector2) : Vector2 = Vector2(this.x + other.x, this.y + other.y)
    operator fun minus(other : Vector2) : Vector2 = Vector2(this.x - other.x, this.y - other.y)
    operator fun times(other: Vector2) : Vector2 = Vector2(this.x * other.x, this.y * other.y)
    operator fun times(other: Float) : Vector2 = Vector2(this.x * other, this.y * other)
}



