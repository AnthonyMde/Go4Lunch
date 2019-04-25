package com.my.anthonymamode.go4lunch.domain

data class Geometry(
    val location: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)