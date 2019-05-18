package com.my.anthonymamode.go4lunch.data.repository

import com.google.android.gms.maps.model.LatLng
import com.my.anthonymamode.go4lunch.data.NetworkModule.Companion.getRetrofit
import com.my.anthonymamode.go4lunch.data.api.GooglePlacesApi
import com.my.anthonymamode.go4lunch.domain.Places
import okhttp3.ResponseBody
import retrofit2.Call

class HomeRepository {

    private val retrofit = getRetrofit().create(GooglePlacesApi::class.java)

    fun getRestaurantPlaces(position: LatLng): Call<Places> {
        return retrofit.getNearbyPlaces("${position.latitude},${position.longitude}", "restaurant")
    }

    fun getPlacePhoto(reference: String?, maxWidth: Int): Call<ResponseBody> {
        return retrofit.getPlacePhoto(reference, maxWidth)
    }
}