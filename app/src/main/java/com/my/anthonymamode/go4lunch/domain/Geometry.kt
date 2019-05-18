package com.my.anthonymamode.go4lunch.domain

import java.io.Serializable

data class Geometry(
    val location: Location
) : Serializable

data class Location(
    val lat: Double,
    val lng: Double
) : Serializable