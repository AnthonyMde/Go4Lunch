package com.my.anthonymamode.go4lunch.domain

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName

data class PlaceDetail(
    val place_id: String,
    val name: String,
    val opening_hours: Hours?,
    val icon: String?,
    val photos: List<PlacePhoto>?,
    var photo: Bitmap?,
    val formatted_address: String?,
    @SerializedName("vicinity")
    val address: String?,
    val rating: Double?,
    val formatted_phone_number: String?,
    val website: String?
)