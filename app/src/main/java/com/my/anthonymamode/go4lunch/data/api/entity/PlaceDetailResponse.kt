package com.my.anthonymamode.go4lunch.data.api.entity

import com.google.gson.annotations.SerializedName
import com.my.anthonymamode.go4lunch.domain.PlaceDetail

data class PlaceDetailResponse(
    @SerializedName("result")
    val placeDetail: PlaceDetail,
    val status: String,
    val error_message: String?
)
