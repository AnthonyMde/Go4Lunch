package com.my.anthonymamode.go4lunch.domain

import com.google.gson.annotations.SerializedName

data class PlaceDetail(
    @SerializedName("result")
    val place: Place,
    val status: String,
    val error_message: String?
)