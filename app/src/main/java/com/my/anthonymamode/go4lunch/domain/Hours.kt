package com.my.anthonymamode.go4lunch.domain

data class Hours(
    val open_now: Boolean,
    val periods: List<Period>
)

data class Period(
    val close: Time,
    val open: Time
)

data class Time(
    val day: Int,
    val time: String
)