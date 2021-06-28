package chapter2

const val pi = 3.1415926535f

fun toDegree(radian: Float): Float = radian * 180f / pi
fun Float.toDegree(): Float = toDegree(this)
