package com.my.anthonymamode.go4lunch.utils

import android.location.Location
import com.google.android.gms.maps.model.LatLng

fun LatLng.distanceTo(locationToCompare: LatLng): Float {
    val location = this
    val loc = Location("now").apply {
        latitude = location.latitude
        longitude = location.longitude
    }
    val locToCompare = Location("last").apply {
        latitude = locationToCompare.latitude
        longitude = locationToCompare.longitude
    }

    return loc.distanceTo(locToCompare)
}

fun LatLng.distanceTo(lat: Double, lng: Double): Float {
    val location = this
    val loc = Location("now").apply {
        latitude = location.latitude
        longitude = location.longitude
    }
    val locToCompare = Location("last").apply {
        latitude = lat
        longitude = lng
    }

    return loc.distanceTo(locToCompare)
}
