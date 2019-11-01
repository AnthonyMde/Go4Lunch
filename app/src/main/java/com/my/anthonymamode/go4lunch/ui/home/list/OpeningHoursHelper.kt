package com.my.anthonymamode.go4lunch.ui.home.list

import android.content.Context
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.domain.Hours
import com.my.anthonymamode.go4lunch.domain.Period
import com.my.anthonymamode.go4lunch.utils.addChar

private const val MAX_MINUTES_IN_DAY = 1440
private const val LAST_DAY_IN_WEEK = 6
private const val FIRST_DAY_IN_WEEK = 0
private const val DAYS_IN_WEEK = 7
private const val HOURS_IN_DAY = 24
private const val MINUTES_IN_HOUR = 60

/**
 * time.first = hours
 * time.second = minutes
 */
fun getTimeInMinutes(time: Pair<Int, Int>): Int {
    require(time.first in 0..HOURS_IN_DAY)
    require(time.second in 0..MINUTES_IN_HOUR)
    return (time.first * MINUTES_IN_HOUR) + time.second
}

/**
 * @param googleTime is a string with format "HHMM"
 * @return pair.fist = hour as int
 * @return pair.second = minute as int
 */
fun googleTimeIntoPair(googleTime: String): Pair<Int, Int> {
    val hours = googleTime.substring(0, googleTime.length - 2).toInt()
    val minutes = googleTime.substring(googleTime.length - 2, googleTime.length).toInt()
    return Pair(hours, minutes)
}

/**
 * Give the interval time between two times (which is a day week (int)
 * and a the day time in minutes (int).
 * @param currentDay actual day of week
 * @param currentTimeInMinutes actual time of the current day
 * @param targetDay day week to target
 * @param targetTimeInMinutes actual time of the target day
 * @return interval in minutes
 */
fun getTimeIntervalInMinutes(
    currentDay: Int,
    currentTimeInMinutes: Int,
    targetDay: Int,
    targetTimeInMinutes: Int
): Int {
    require(currentDay in FIRST_DAY_IN_WEEK..LAST_DAY_IN_WEEK && targetDay in FIRST_DAY_IN_WEEK..LAST_DAY_IN_WEEK)
    require(currentTimeInMinutes in 0 until MAX_MINUTES_IN_DAY && targetTimeInMinutes in 0 until MAX_MINUTES_IN_DAY)

    val sortedDays = (currentDay..LAST_DAY_IN_WEEK) + (FIRST_DAY_IN_WEEK until currentDay)
    // specific computation if target day equal current day but is in the past
    return if (currentDay == targetDay && currentTimeInMinutes > targetTimeInMinutes) {
        DAYS_IN_WEEK * HOURS_IN_DAY * MINUTES_IN_HOUR - (currentTimeInMinutes - targetTimeInMinutes)
    } else {
        val dayInterval = sortedDays.indexOf(targetDay)
        val dayIntervalInMinutes = dayInterval * HOURS_IN_DAY * MINUTES_IN_HOUR
        dayIntervalInMinutes + (targetTimeInMinutes - currentTimeInMinutes)
    }
}

/**
 * According to the current state of the restaurant (open or close)
 * @return the next time the restaurant is opened or closed.
 */
fun getTheNextRestaurantHours(
    hours: Hours,
    currentDay: Int,
    currentHour: Int,
    currentMinute: Int
): Period? {
    val periods = hours.periods

    // Sorted by smaller time interval from current day
    val sortedPeriods = periods.sortedWith(
        compareBy {
            val targetTime = if (hours.open_now) it.closeTime else it.openTime
            getTimeIntervalInMinutes(
                currentDay,
                getTimeInMinutes(Pair(currentHour, currentMinute)),
                targetTime.day,
                getTimeInMinutes(googleTimeIntoPair(targetTime.time))
            )
        }
    )
    return sortedPeriods.firstOrNull()
}

/**
 * @return the text which presents to the user the next opening
 * or closing of the restaurant.
 */
fun getRestaurantOpeningText(
    period: Period?,
    forOpening: Boolean,
    currentDay: Int,
    context: Context
): String {
    if (period == null) {
        return if (forOpening) {
            context.getString(R.string.restaurant_item_permanently_closed)
        } else {
            context.getString(R.string.restaurant_item_permanently_opened)
        }
    }

    val targetTime = if (forOpening) period.openTime else period.closeTime
    val dayText = if (targetTime.day == currentDay) "" else getDay(targetTime.day, context)
    val timeText = targetTime.time.addChar(':', targetTime.time.length / 2)
    val startText = if (forOpening) {
        context.getString(R.string.restaurant_item_opening_start_text)
    } else {
        context.getString(R.string.restaurant_item_closing_start_text)
    }

    return StringBuilder()
        .append(startText)
        .append(" ")
        .append(if (dayText.isNotEmpty()) "$dayText " else "")
        .append("at $timeText")
        .toString()
}

/**
 * @return the current day as is week name.
 */
fun getDay(dayNumber: Int, context: Context): String {
    return when (dayNumber) {
        0 -> context.getString(R.string.restaurant_item_sunday)
        1 -> context.getString(R.string.restaurant_item_monday)
        2 -> context.getString(R.string.restaurant_item_tuesday)
        3 -> context.getString(R.string.restaurant_item_wednesday)
        4 -> context.getString(R.string.restaurant_item_thursday)
        5 -> context.getString(R.string.restaurant_item_friday)
        6 -> context.getString(R.string.restaurant_item_saturday)
        else -> ""
    }
}
