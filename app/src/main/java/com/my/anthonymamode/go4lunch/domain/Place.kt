package com.my.anthonymamode.go4lunch.domain

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// todo : remove serializable if not used
data class Place(
    val place_id: String,
    val name: String,
    val opening_hours: Hours?,
    val icon: String?,
    val photos: List<PlacePhoto>?,
    val formatted_address: String?,
    @SerializedName("vicinity")
    val address: String?,
    val geometry: Geometry,
    val rating: Double?,
    val formatted_phone_number: String?,
    val website: String?
) : Serializable