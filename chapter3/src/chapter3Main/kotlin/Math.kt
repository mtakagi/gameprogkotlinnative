package chapter3

import kotlin.math.absoluteValue

const val pi = 3.1415926535f
const val pi2 = pi * 2

fun toDegree(radian: Float): Float = radian * 180f / pi
fun Float.toDegree(): Float = toDegree(this)
fun Float.nearZero(epsilon : Float = 0.001f) : Boolean = this.absoluteValue <= epsilon

