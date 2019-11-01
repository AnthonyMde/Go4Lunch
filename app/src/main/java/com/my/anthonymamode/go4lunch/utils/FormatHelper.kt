package com.my.anthonymamode.go4lunch.utils

import java.text.DecimalFormat
import kotlin.math.roundToInt

fun Float.toFormatDistance(): String {
    if (this < 0)
        return ""

    return if (this < 1000) {
        "${this.roundToInt()}m"
    } else {
        val kilometers = DecimalFormat("#.#").format(this / 1000)
        "${kilometers}km"
    }
}

fun Double.toStarsFormat(): Float {
    return when ((this * 0.6).toFloat()) { // From 5 stars rating to 3 stars rating
        in 0.5f..1f -> 1f
        in 1f..2f -> 2f
        in 2f..3f -> 3f
        else -> -1f
    }
}
