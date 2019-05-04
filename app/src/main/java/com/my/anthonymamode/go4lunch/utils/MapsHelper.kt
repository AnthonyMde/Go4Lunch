package com.my.anthonymamode.go4lunch.utils

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.my.anthonymamode.go4lunch.domain.Place

class MapsHelper(private val googleMap: GoogleMap?) {

    private var mapsCenter: LatLng? = null

    fun recenterMap(currentPosition: LatLng, zoom: Float) {
        googleMap?.apply {
            moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    currentPosition,
                    zoom
                )
            )
            mapsCenter = cameraPosition.target
        }
    }

    fun setMapsCenter(center: LatLng) {
        mapsCenter = center
    }

    fun getMapsCenter(): LatLng? {
        return mapsCenter
    }

    fun setRestaurantMarkers(restaurants: List<Place>) {
        googleMap?.clear()
        for (restaurant in restaurants) {
            val latLng = LatLng(restaurant.geometry.location.lat, restaurant.geometry.location.lng)
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            googleMap?.addMarker(markerOptions)
        }
    }
}