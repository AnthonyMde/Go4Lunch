package com.my.anthonymamode.go4lunch.ui.detail

import androidx.lifecycle.ViewModel
import com.my.anthonymamode.go4lunch.data.repository.PlacesRepository
import com.my.anthonymamode.go4lunch.domain.PlaceDetail
import okhttp3.ResponseBody
import retrofit2.Call

class DetailRestaurantViewModel : ViewModel() {
    private val repository = PlacesRepository()

    fun getPlacePhoto(reference: String?, maxWidth: Int): Call<ResponseBody> {
        return repository.getPlacePhoto(reference, maxWidth)
    }

    fun getPlaceDetail(placeId: String): Call<PlaceDetail> {
        return repository.getPlaceDetail(placeId)
    }
}