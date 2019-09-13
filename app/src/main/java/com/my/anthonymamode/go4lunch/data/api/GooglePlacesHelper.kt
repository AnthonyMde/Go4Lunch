package com.my.anthonymamode.go4lunch.data.api

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest

fun getPredictions(query: String, startPoint: LatLng, endPoint: LatLng): FindAutocompletePredictionsRequest {
    // Create a new token for the autocomplete session.
    val token = AutocompleteSessionToken.newInstance()
    // Create a RectangularBounds object.
    val bounds = RectangularBounds.newInstance(
        startPoint,
        endPoint
    )

    // Use the builder to create a FindAutocompletePredictionsRequest.
    return FindAutocompletePredictionsRequest.builder()
        .setSessionToken(token)
        .setLocationBias(bounds)
        .setTypeFilter(TypeFilter.ESTABLISHMENT)
        .setQuery(query)
        .build()
}
