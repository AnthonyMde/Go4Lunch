package com.my.anthonymamode.go4lunch.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.ui.home.HomeActivity
import com.my.anthonymamode.go4lunch.ui.home.HomeViewModel
import com.my.anthonymamode.go4lunch.utils.base.BaseActivity
import com.my.anthonymamode.go4lunch.utils.Resource
import com.my.anthonymamode.go4lunch.utils.setStatusBarTransparent
import kotlinx.android.synthetic.main.activity_permission.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity

private const val PERMISSION_REQUEST = 99

class PermissionActivity : BaseActivity() {

    private val viewModel: HomeViewModel by lazy {
        ViewModelProviders.of(this).get(HomeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
        window.setStatusBarTransparent()
        setObservers()
        setListeners()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity<HomeActivity>()
                    this.finish()
                }
            }
        }
    }

    private fun setObservers() {
        viewModel.logout.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    startActivity(intentFor<LoginActivity>().newTask().clearTask())
                    finish()
                }
                is Resource.Error -> {
                    showToastError(getString(R.string.logout_error))
                    Log.e("Firebase error", "Can't log out user : ${it.error.message}")
                }
            }
        })
    }

    private fun setListeners() {
        permissionAgreeButton.setOnClickListener {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST)
        }
        permissionLogoutButton.setOnClickListener {
            viewModel.logoutUser()
        }
    }
}
