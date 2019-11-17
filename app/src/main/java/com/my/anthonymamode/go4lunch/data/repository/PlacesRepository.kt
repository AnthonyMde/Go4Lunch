package com.my.anthonymamode.go4lunch.data.repository

import android.graphics.BitmapFactory
import com.google.android.gms.maps.model.LatLng
import com.my.anthonymamode.go4lunch.data.NetworkModule.Companion.getRetrofitPlaces
import com.my.anthonymamode.go4lunch.data.api.GooglePlacesApi
import com.my.anthonymamode.go4lunch.domain.Place
import com.my.anthonymamode.go4lunch.domain.PlaceDetail
import com.my.anthonymamode.go4lunch.ui.home.list.radiusSearch
import io.reactivex.Single
import org.jetbrains.anko.collections.forEachWithIndex

private const val MAX_PHOTO_WIDTH = 1280

class PlacesRepository {
    private val retrofit = getRetrofitPlaces().create(GooglePlacesApi::class.java)

    /**
     * Gets restaurant by radius area search.
     * If there is more than 20 places as result, we launch an extra API call to get
     * more places.
     */
    fun getRestaurantPlacesByRadius(position: LatLng): Single<List<Place>> {
        val listPlaces = mutableListOf<Place>()
        return retrofit.getPlacesbyRadius(
            "${position.latitude},${position.longitude}",
            "restaurant",
            radiusSearch
        )
            .flatMap {
                listPlaces.addAll(it.places)

                // If there is more than twenty results we relaunch the request one time
                if (it.next_page_token != null) {
                    retrofit.getPlacesbyRadius(
                        "${position.latitude},${position.longitude}",
                        "restaurant",
                        radiusSearch,
                        it.next_page_token
                    )
                } // Else we just let it go
                else {
                    Single.just(it)
                }
            }
            .map {
                listPlaces.addAll(listPlaces.size, it.places)
                listPlaces
            }
    }

    /**
     * A first call gets a list of places, then we go through the list to get
     * opening hours for each of the places we have.
     * @return the list of places containing the Hours object.
     */
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

    /**
     * Launches a first API call to get all details wanted for a specific place and then an
     * additional call to get the place associated photo.
     * @param placeId the id of the place.
     * @return an observable containing the PlaceDetail object.
     */
    fun getPlaceDetail(placeId: String): Single<PlaceDetail> {
        val fieldsNeeded =
            "place_id,name,formatted_address,rating,photo,formatted_phone_number,website"
        return retrofit.getPlaceDetail(placeId, fieldsNeeded).flatMap { placeResponse ->
            retrofit.getPlacePhoto(
                placeResponse.placeDetail.photos?.get(0)?.photo_reference,
                MAX_PHOTO_WIDTH
            ).map {
                val restaurant = placeResponse.placeDetail
                restaurant.photo = BitmapFactory.decodeStream(it.byteStream())
                restaurant
            }
        }
    }
}
