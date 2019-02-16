package com.my.anthonymamode.go4lunch.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.my.anthonymamode.go4lunch.utils.Resource

/**
 * ViewModel scoped to the HomeActivity lifecycle. Data hold by this
 * ViewModel are shared by the HomeActivity and its child fragments.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {
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

    /**
     * @return the FirebaseUser object which contains his data.
     */
    fun getUserInfo() {
        val infoUser = FirebaseAuth.getInstance().currentUser
        if (infoUser != null) {
            _userInfo.postValue(Resource.Success(infoUser))
        }
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
}