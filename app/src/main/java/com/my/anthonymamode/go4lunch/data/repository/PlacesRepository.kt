package com.my.anthonymamode.go4lunch.data.repository

import android.graphics.BitmapFactory
import com.google.android.gms.maps.model.LatLng
import com.my.anthonymamode.go4lunch.data.NetworkModule.Companion.getRetrofitPlaces
import com.my.anthonymamode.go4lunch.data.api.GooglePlacesApi
import com.my.anthonymamode.go4lunch.data.api.entity.PlaceResponseWrapper
import com.my.anthonymamode.go4lunch.domain.PlaceDetail
import io.reactivex.Single
import retrofit2.Call

private const val MAX_PHOTO_WIDTH = 1280

class PlacesRepository {

    private val retrofit = getRetrofitPlaces().create(GooglePlacesApi::class.java)

    fun getRestaurantPlaces(position: LatLng): Call<PlaceResponseWrapper> {
        return retrofit.getNearbyPlaces("${position.latitude},${position.longitude}", "restaurant")
    }

    fun getPlaceDetail(placeId: String): Single<PlaceDetail> {
        val fieldsNeeded = "place_id,name,formatted_address,rating,photo,formatted_phone_number,website"
        return retrofit.getPlaceDetail(placeId, fieldsNeeded).flatMap { placeResponse ->
            retrofit.getPlacePhoto(placeResponse.placeDetail.photos?.get(0)?.photo_reference, MAX_PHOTO_WIDTH).map {
                val restaurant = placeResponse.placeDetail
                restaurant.photo = BitmapFactory.decodeStream(it.byteStream())
                restaurant
            }
        }
    }
}