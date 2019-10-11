package com.my.anthonymamode.go4lunch.utils.base

import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.my.anthonymamode.go4lunch.R

abstract class BaseActivity : AppCompatActivity() {

    private val loadingView: View by lazy { findViewById<View>(R.id.base_loader) }
    private val contentView: View by lazy { findViewById<View>(R.id.contentView) }

    fun showToastError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun showContent() {
        loadingView.visibility = GONE
        contentView.visibility = VISIBLE
    }

    fun showLoader() {
        loadingView.visibility = VISIBLE
        contentView.visibility = GONE
    }

    fun onFailureListener(): OnFailureListener {
        return OnFailureListener {
            showToastError(getString(R.string.firebase_crud_error))
            Log.e("FIREBASE ERROR", "Error when updating data from activity : ${it.message}")
        }
    }
}
