package com.my.anthonymamode.go4lunch.domain

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName

data class Place(
    val name: String,
    val place_id: String,
    val geometry: Geometry,
    val icon: String?,
    val rating: Double?,
    @SerializedName("vicinity")
    val address: String?,
    var opening_hours: Hours?,
    val photos: List<PlacePhoto>?,
    var photo: Bitmap?
)