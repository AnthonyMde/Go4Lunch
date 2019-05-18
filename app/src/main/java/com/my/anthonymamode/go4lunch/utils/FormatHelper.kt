package com.my.anthonymamode.go4lunch.utils

import java.text.DecimalFormat
import kotlin.math.roundToInt

fun Float.toFormatDistance(): String {
    if (this < 0)
        return ""

    if (this < 1000) {
        return "${this.roundToInt()}m"
    } else {
        val kilometers = DecimalFormat("#.#").format(this / 1000)
        return "${kilometers}km"
    }
}

fun toStarsFormat(rating: Double?): Float {
    return if (rating != null) {
        when ((rating * 0.6).toFloat()) { // From 5 stars rating to 3 stars rating
            in 0.5f..1f -> 1f
            in 1f..2f -> 2f
            in 2f..3f -> 3f
            else -> -1f
        }
    } else {
        return -1f
    }
}