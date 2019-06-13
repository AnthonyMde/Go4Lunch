package com.my.anthonymamode.go4lunch.data.repository

import com.google.android.gms.maps.model.LatLng
import com.my.anthonymamode.go4lunch.data.NetworkModule.Companion.getRetrofitPlaces
import com.my.anthonymamode.go4lunch.data.api.GooglePlacesApi
import com.my.anthonymamode.go4lunch.domain.PlaceDetail
import com.my.anthonymamode.go4lunch.domain.Places
import okhttp3.ResponseBody
import retrofit2.Call

class PlacesRepository {

    private val retrofit = getRetrofitPlaces().create(GooglePlacesApi::class.java)

    fun getRestaurantPlaces(position: LatLng): Call<Places> {
        return retrofit.getNearbyPlaces("${position.latitude},${position.longitude}", "restaurant")
    }

    fun getPlacePhoto(reference: String?, maxWidth: Int): Call<ResponseBody> {
        return retrofit.getPlacePhoto(reference, maxWidth)
    }

    fun getPlaceDetail(placeId: String): Call<PlaceDetail> {
        val fieldsNeeded = "place_id,name,formatted_address,rating,photo,formatted_phone_number,website"
        return retrofit.getPlaceDetail(placeId, fieldsNeeded)
    }
}