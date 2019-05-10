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