package com.my.anthonymamode.go4lunch.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.my.anthonymamode.go4lunch.data.repository.PlacesRepository
import com.my.anthonymamode.go4lunch.domain.Place
import com.my.anthonymamode.go4lunch.utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

/**
 * ViewModel scoped to the HomeActivity lifecycle. Data hold by this
 * ViewModel are shared by the HomeActivity and its child fragments.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val compositeDisposable = CompositeDisposable()
    private val repository = PlacesRepository()
    /**
     * For each value that we want to expose, we have two variables : a private MutableLiveData,
     * only modifiable by this ViewModel and a public LiveData, readable by the observer.
     */
    private var _logout = MutableLiveData<Resource<Boolean>>()
    val logout: LiveData<Resource<Boolean>>
        get() = _logout

    private var _userInfo = MutableLiveData<Resource<FirebaseUser?>>()
    val userInfo: LiveData<Resource<FirebaseUser?>>
        get() = _userInfo

    private var _lastLocation = MutableLiveData<LatLng>()
    val lastLocation: LiveData<LatLng>
        get() = _lastLocation

    private var _placeList = MutableLiveData<Resource<List<Place>>>()
    val placeList: LiveData<Resource<List<Place>>>
        get() = _placeList

    private var _placeWithHoursList = MutableLiveData<Resource<List<Place>>>()
    val placeWithHoursList: LiveData<Resource<List<Place>>>
        get() = _placeWithHoursList

    var userId: String? = null

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    /**
     * @return the FirebaseUser object which contains his data.
     */
    fun getUserInfo() {
        val infoUser = FirebaseAuth.getInstance().currentUser
        if (infoUser != null) {
            _userInfo.postValue(Resource.Success(infoUser))
            userId = infoUser.uid
        }
    }

    fun getRestaurantPlaces(position: LatLng) {
        repository.getRestaurantPlaces(position)
            .doOnSubscribe { _placeList.postValue(Resource.Loading()) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _placeList.postValue(Resource.Success(it))
            }, {
                _placeList.postValue(Resource.Error(it))
            })
            .addTo(compositeDisposable)
    }

    fun getRestaurantPlacesWithHours(position: LatLng) {
        repository.getRestaurantPlacesWithHours(position)
            .doOnSubscribe { _placeWithHoursList.postValue(Resource.Loading()) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _placeWithHoursList.postValue(Resource.Success(it))
            }, {
                _placeWithHoursList.postValue(Resource.Error(it))
            })
            .addTo(compositeDisposable)
    }

    /**
     * Log the current user out of Firebase account.
     */
    fun logoutUser() {
        AuthUI.getInstance()
            .signOut(getApplication())
            .addOnSuccessListener {
                _logout.postValue(Resource.Success(true))
            }
            .addOnFailureListener {
                _logout.postValue(Resource.Error(it))
            }
    }

    fun setLastLocation(lastLocation: LatLng) {
        _lastLocation.postValue(lastLocation)
    }
}