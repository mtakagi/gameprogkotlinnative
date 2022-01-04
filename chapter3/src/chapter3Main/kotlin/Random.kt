package chapter3

import kotlin.random.Random

@ThreadLocal
object Random {

    private lateinit var rand: Random

    fun init() {
        seed(Random.Default.nextInt())
    }

    fun seed(seed: Int) {
        rand = Random(seed)
    }

    fun getFloatRange(min: Float, max: Float) : Float
    {
        return rand.nextDouble(min.toDouble(), max.toDouble()).toFloat()
    }

    fun getFloat() : Float = rand.nextFloat()

    fun getVector(min: Vector2, max: Vector2): Vector2
    {
        val vec = Vector2(x = getFloat(), y = getFloat())

        return min + (max - min) * vec
    }
}