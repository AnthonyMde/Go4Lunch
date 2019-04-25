package com.my.anthonymamode.go4lunch.domain

data class PlacePhoto(
    val height: Int,
    val width: Int,
    val html_attributions: List<String>,
    val photo_reference: String
)