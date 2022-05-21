package chapter3

class CircleComponent(private val owner: Actor) : Component(owner) {
    var radius = 0.0f

    fun getCenter() : Vector2 = owner.position
    fun getRadius() : Float = owner.scale * radius
}

fun intersect(a : CircleComponent, b : CircleComponent) : Boolean
{
    val diff = a.getCenter() - b.getCenter()
    val lengthSq = diff.lengthSq()
    var radiiSq = a.getRadius() + b.getRadius()
    radiiSq *= radiiSq

    return lengthSq <= radiiSq
}