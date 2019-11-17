package com.my.anthonymamode.go4lunch.utils

import java.text.DecimalFormat
import kotlin.math.roundToInt

/**
 * Rounded float to int to display meters to user.
 * If distance is greater than 1000m, we format the text
 * to display km to the user.
 */
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

/**
 * Takes the 5 range stars format from google API and turns it into
 * a 4 range stars format. But as we do not show stars to user until we reach
 * at least one star, we have to reduce the total by one.
 */
fun setStarsFormat(googleRating: Double?): Float {
    googleRating ?: return -1f
    return ((googleRating * 0.8).toFloat()) - 1f
}
