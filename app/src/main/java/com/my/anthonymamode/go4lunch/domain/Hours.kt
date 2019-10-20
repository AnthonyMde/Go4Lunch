package com.my.anthonymamode.go4lunch.domain

import com.google.gson.annotations.SerializedName

data class Hours(
    val open_now: Boolean,
    val periods: List<Period>
)

data class Period(
    @SerializedName("close")
    val closeTime: Time,
    @SerializedName("open")
    val openTime: Time
)

data class Time(
    val day: Int,
    val time: String
)
