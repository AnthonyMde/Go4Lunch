package com.my.anthonymamode.go4lunch.data.api

import com.my.anthonymamode.go4lunch.data.api.entity.PlaceDetailResponse
import com.my.anthonymamode.go4lunch.data.api.entity.PlaceResponse
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlacesApi {
    @GET("nearbysearch/json?key=$API_KEY_GOOGLE_PLACES")
    fun getPlacesByDistance(
        @Query("location") location: String,
        @Query("type") type: String,
        @Query("rankby") rankby: String = "distance"
    ): Single<PlaceResponse>

    @GET("nearbysearch/json?key=$API_KEY_GOOGLE_PLACES")
    fun getPlacesbyRadius(
        @Query("location") location: String,
        @Query("type") type: String,
        @Query("radius") radius: Double,
        @Query("pagetoken") token: String? = null
    ): Single<PlaceResponse>

    @GET("details/json?key=$API_KEY_GOOGLE_PLACES")
    fun getPlaceDetail(
        @Query("placeid") placeId: String,
        @Query("fields") fields: String
    ): Single<PlaceDetailResponse>

    @GET("photo?key=$API_KEY_GOOGLE_PLACES")
    fun getPlacePhoto(
        @Query("photoreference") reference: String?,
        @Query("maxwidth") maxWidth: Int
    ): Single<ResponseBody>
}
