package com.my.anthonymamode.go4lunch.ui

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.ui.home.HomeViewModel
import com.my.anthonymamode.go4lunch.utils.BaseActivity
import com.my.anthonymamode.go4lunch.utils.Resource
import kotlinx.android.synthetic.main.activity_permission.*
import org.jetbrains.anko.startActivity

private const val PERMISSION_REQUEST = 99

class PermissionActivity : BaseActivity() {

    private val viewModel: HomeViewModel by lazy {
        ViewModelProviders.of(this).get(HomeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
        setObservers()
        setListeners()
    }

    private fun setObservers() {
        viewModel.logout.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    startActivity<LoginActivity>()
                    finish()
                }
                is Resource.Error -> {
                    showContent()
                    showToastError(getString(R.string.logout_error))
                    Log.e("Firebase error", "Can't log out user : ${it.error.message}")
                }
            }
        })
    }

    private fun setListeners() {
        permissionAgreeButton.setOnClickListener {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSION_REQUEST)
        }
        permissionLogoutButton.setOnClickListener {
            showLoader()
            viewModel.logoutUser()
        }
    }
}
