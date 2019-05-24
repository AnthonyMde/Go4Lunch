package com.my.anthonymamode.go4lunch.ui.detail

import androidx.lifecycle.ViewModel
import com.my.anthonymamode.go4lunch.data.repository.HomeRepository
import okhttp3.ResponseBody
import retrofit2.Call

class DetailRestaurantViewModel : ViewModel() {
    private val repository = HomeRepository()

    fun getPlacePhoto(reference: String?, maxWidth: Int): Call<ResponseBody> {
        return repository.getPlacePhoto(reference, maxWidth)
    }
}