package com.my.anthonymamode.go4lunch.domain

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName
import java.io.Serializable

// todo : remove serializable if not used
data class Place(
    val name: String,
    val place_id: String,
    val geometry: Geometry,
    val icon: String?,
    val rating: Double?,
    @SerializedName("vicinity")
    val address: String?,
    val opening_hours: Hours?,
    val photos: List<PlacePhoto>?,
    var photo: Bitmap?
) : Serializable