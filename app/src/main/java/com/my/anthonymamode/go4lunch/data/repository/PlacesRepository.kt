package com.my.anthonymamode.go4lunch.data.repository

import android.graphics.BitmapFactory
import com.google.android.gms.maps.model.LatLng
import com.my.anthonymamode.go4lunch.data.NetworkModule.Companion.getRetrofitPlaces
import com.my.anthonymamode.go4lunch.data.api.GooglePlacesApi
import com.my.anthonymamode.go4lunch.domain.Place
import com.my.anthonymamode.go4lunch.domain.PlaceDetail
import com.my.anthonymamode.go4lunch.ui.home.list.RestaurantListFragment.Companion.radiusSearch
import io.reactivex.Single
import org.jetbrains.anko.collections.forEachWithIndex

class PlacesRepository {
    private val maxPhotoWidth = 1280
    private val retrofit = getRetrofitPlaces().create(GooglePlacesApi::class.java)

    fun getRestaurantPlacesByRadius(position: LatLng): Single<List<Place>> {
        return retrofit.getPlacesbyRadius(
            "${position.latitude},${position.longitude}",
            "restaurant",
            radiusSearch
        )
            .map {
                it.places
            }
    }

    fun getRestaurantPlacesWithHours(position: LatLng): Single<List<Place>> {
        return retrofit.getPlacesByDistance(
            "${position.latitude},${position.longitude}",
            "restaurant"
        )
            .map {
                it.places
            }
            .flatMap { placeList ->
                val listObservable = placeList.map { getOpeningsHours(it.place_id) }
                Single.zip(listObservable) {
                    val list = it.toList() as List<PlaceDetail>
                    list.forEachWithIndex { index, placeDetail ->
                        placeList[index].opening_hours = placeDetail.opening_hours
                    }
                    return@zip placeList
                }
            }
    }

    private fun getOpeningsHours(placeId: String): Single<PlaceDetail> {
        return retrofit.getPlaceDetail(placeId, "opening_hours").map {
            it.placeDetail
        }
    }

    fun getPlaceDetail(placeId: String): Single<PlaceDetail> {
        val fieldsNeeded =
            "place_id,name,formatted_address,rating,photo,formatted_phone_number,website"
        return retrofit.getPlaceDetail(placeId, fieldsNeeded).flatMap { placeResponse ->
            retrofit.getPlacePhoto(
                placeResponse.placeDetail.photos?.get(0)?.photo_reference,
                maxPhotoWidth
            ).map {
                val restaurant = placeResponse.placeDetail
                restaurant.photo = BitmapFactory.decodeStream(it.byteStream())
                restaurant
            }
        }
    }
}
