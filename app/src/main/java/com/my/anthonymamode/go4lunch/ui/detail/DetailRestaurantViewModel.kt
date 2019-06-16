package com.my.anthonymamode.go4lunch.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.my.anthonymamode.go4lunch.data.repository.PlacesRepository
import com.my.anthonymamode.go4lunch.domain.PlaceDetail
import com.my.anthonymamode.go4lunch.utils.BaseViewModel
import com.my.anthonymamode.go4lunch.utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class DetailRestaurantViewModel : BaseViewModel() {
    private val repository = PlacesRepository()
    private var _placeDetail = MutableLiveData<Resource<PlaceDetail>>()
    val placeDetail: LiveData<Resource<PlaceDetail>>
        get() = _placeDetail

    fun getPlaceDetail(placeId: String) {
        repository.getPlaceDetail(placeId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _placeDetail.postValue(Resource.Loading()) }
            .subscribe({
                _placeDetail.postValue(Resource.Success(it))
            }, {
                _placeDetail.postValue(Resource.Error(it))
            })
            .addTo(disposables)
    }
}