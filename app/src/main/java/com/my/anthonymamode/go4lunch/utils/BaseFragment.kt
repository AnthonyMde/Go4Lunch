package com.my.anthonymamode.go4lunch.utils

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnFailureListener
import com.my.anthonymamode.go4lunch.R

abstract class BaseFragment : Fragment() {

    fun onFailureListener(): OnFailureListener {
        return OnFailureListener {
            showToastError(getString(R.string.firebase_crud_error))
            Log.e("FIREBASE ERROR", "Error when updating data from fragment : ${it.message}")
        }
    }

    fun showToastError(message: String) {
        Toast.makeText(this.context, message, Toast.LENGTH_LONG).show()
    }

    fun showMessage(message: String) {
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
    }
}