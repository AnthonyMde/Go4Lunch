package com.my.anthonymamode.go4lunch

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.my.anthonymamode.go4lunch.utils.Resource

/**
 * ViewModel scoped to the HomeActivity lifecycle. Data hold by this
 * ViewModel are shared by the HomeActivity and its child fragments.
 */
class HomeViewModel(application: Application): AndroidViewModel(application) {
    private var _logout = MutableLiveData<Resource<Boolean>>()
    val logout: LiveData<Resource<Boolean>>
        get() = _logout

    /**
     * @return the FirebaseUser object which contains his data.
     */
    fun getUserInfo(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
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