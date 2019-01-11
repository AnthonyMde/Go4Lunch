package com.my.anthonymamode.go4lunch

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 123
    private val googleProvider = arrayListOf(
        AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
    )
    private val facebookProvider = arrayListOf(
        AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginGoogleButton.setOnClickListener {
            startSignin(googleProvider)
        }
        loginFacebookButton.setOnClickListener {
            startSignin(facebookProvider)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        redirectAfterSignin(requestCode, resultCode, data)
    }

    private fun startSignin(provider : ArrayList<AuthUI.IdpConfig>) {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(provider)
                .build(),
            RC_SIGN_IN
        )
    }

    private fun redirectAfterSignin(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            when (resultCode) {
                Activity.RESULT_OK -> launchHomeActivity()
                Activity.RESULT_CANCELED -> {
                    val response = IdpResponse.fromResultIntent(data)
                    when {
                        response == null -> showCanceledSnackBar(R.string.login_canceled)
                        response.errorCode == ErrorCodes.NO_NETWORK -> showCanceledSnackBar(R.string.login_no_network)
                        response.errorCode == ErrorCodes.UNKNOWN_ERROR -> showCanceledSnackBar(R.string.login_unknown_error)
                    }
                }
            }
        }
    }

    private fun launchHomeActivity() {
        intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun showCanceledSnackBar(errorMessage: Int) {
        Snackbar.make(loginActivityLayout, errorMessage, Snackbar.LENGTH_SHORT)
    }
}
