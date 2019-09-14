package com.my.anthonymamode.go4lunch.data.api

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.maps.android.SphericalUtil
import kotlin.math.sqrt

fun getPredictions(
    query: String,
    bounds: RectangularBounds?
): FindAutocompletePredictionsRequest {
    // Create a new token for the autocomplete session.
    val token = AutocompleteSessionToken.newInstance()
    // Use the builder to create a FindAutocompletePredictionsRequest.
    return FindAutocompletePredictionsRequest.builder()
        .setSessionToken(token)
        .setLocationBias(bounds)
        .setTypeFilter(TypeFilter.ESTABLISHMENT)
        .setQuery(query)
        .build()
}

fun toRectangularBounds(center: LatLng?, radiusInMeters: Double): RectangularBounds? {
    if (center == null) {
        return null
    }
    val distanceFromCenterToCorner = radiusInMeters * sqrt(2.0)
    val southWestCorner = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0)
    val northEastCorner = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0)
    return RectangularBounds.newInstance(LatLngBounds(southWestCorner, northEastCorner))
}
