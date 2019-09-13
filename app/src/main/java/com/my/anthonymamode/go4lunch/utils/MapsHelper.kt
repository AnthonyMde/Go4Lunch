package com.my.anthonymamode.go4lunch.utils

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.data.api.getUsersByLunchId
import com.my.anthonymamode.go4lunch.domain.Place

class MapsHelper(private val googleMap: GoogleMap?) {

    private var mapsCenter: LatLng? = null

    fun centerMap(currentPosition: LatLng, zoom: Float) {
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

    fun setRestaurantMarkers(restaurants: List<Place>, markerOnClick: (String) -> Unit) {
        var icon = R.drawable.ic_maps_marker_red
        googleMap?.clear()
        googleMap?.setOnMarkerClickListener {
            markerOnClick.invoke(it.tag as String)
            true
        }

        for (restaurant in restaurants) {
            val latLng = LatLng(restaurant.geometry.location.lat, restaurant.geometry.location.lng)

            getUsersByLunchId(restaurant.place_id).get()
                .addOnSuccessListener {
                    if (it.documents.size > 0) {
                        icon = R.drawable.ic_maps_marker_green
                    }
                    setMarker(icon, latLng, restaurant)
                }
                .addOnFailureListener {
                    setMarker(icon, latLng, restaurant)
                }
        }
    }

    fun displaySelectedRestaurants(searchedPlaces: List<AutocompletePrediction>, currentRestaurantList: List<Place>?, markerOnClick: (String) -> Unit) {
        if (currentRestaurantList == null) {
            return
        }

        val matchingRestaurants = currentRestaurantList.filter { currentPlace ->
            searchedPlaces.map { it.placeId }.contains(currentPlace.place_id)
        }

        setRestaurantMarkers(matchingRestaurants, markerOnClick)
    }

    fun getNorthEastBounds(): LatLng? {
        return googleMap?.projection?.visibleRegion?.latLngBounds?.northeast
    }

    fun getSouthWestBounds(): LatLng? {
        return googleMap?.projection?.visibleRegion?.latLngBounds?.southwest
    }

    private fun setMarker(icon: Int, latLng: LatLng, restaurant: Place) {
        val markerOptions = MarkerOptions()
        markerOptions.apply {
            position(latLng)
            title(restaurant.name)
            icon(BitmapDescriptorFactory.fromResource(icon))
        }
        val marker = googleMap?.addMarker(markerOptions)
        marker?.tag = restaurant.place_id
    }
}
