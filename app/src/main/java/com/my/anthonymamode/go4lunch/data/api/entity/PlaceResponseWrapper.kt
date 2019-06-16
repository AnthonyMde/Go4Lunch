package com.my.anthonymamode.go4lunch.data.api.entity

import com.google.gson.annotations.SerializedName
import com.my.anthonymamode.go4lunch.domain.Place

data class PlaceResponseWrapper(
    @SerializedName("results")
    val places: List<Place>,
    val status: String,
    val error_message: String?
)