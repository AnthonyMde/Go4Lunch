package com.my.anthonymamode.go4lunch.domain

import com.google.gson.annotations.SerializedName

data class Place(
    val geometry: Geometry,
    val icon: String?,
    val id: String?,
    val name: String,
    val opening_hours: Hours?,
    val photos: List<PlacePhoto>?,
    val place_id: String?,
    val rating: Double?,
    val user_ratings_total: Double?,
    @SerializedName("vicinity")
    val address: String?
)