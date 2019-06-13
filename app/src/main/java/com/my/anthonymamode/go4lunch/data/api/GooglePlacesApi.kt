package com.my.anthonymamode.go4lunch.data.api

import com.my.anthonymamode.go4lunch.domain.PlaceDetail
import com.my.anthonymamode.go4lunch.domain.Places
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlacesApi {
    @GET("nearbysearch/json?key=$API_KEY_GOOGLE_PLACES")
    fun getNearbyPlaces(@Query("location") location: String, @Query("type") type: String, @Query("rankby") rankby: String = "distance"): Call<Places>

    @GET("details/json?key=$API_KEY_GOOGLE_PLACES")
    fun getPlaceDetail(@Query("placeid") placeId: String, @Query("fields") fields: String): Call<PlaceDetail>

    @GET("photo?key=$API_KEY_GOOGLE_PLACES")
    fun getPlacePhoto(@Query("photoreference") reference: String?, @Query("maxwidth") maxWidth: Int): Call<ResponseBody>
}