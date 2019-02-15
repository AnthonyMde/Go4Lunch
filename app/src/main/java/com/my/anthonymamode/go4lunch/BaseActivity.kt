package com.my.anthonymamode.go4lunch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast

abstract class BaseActivity : AppCompatActivity() {

    val loadingView: View by lazy { findViewById<View>(R.id.base_loader) }
    val contentView: View by lazy { findViewById<View>(R.id.contentView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
}
