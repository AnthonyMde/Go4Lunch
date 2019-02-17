package com.my.anthonymamode.go4lunch.ui

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.api.createUser
import com.my.anthonymamode.go4lunch.ui.home.HomeActivity
import com.my.anthonymamode.go4lunch.utils.BaseActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity

class LoginActivity : BaseActivity() {
    companion object {
        // Use to know which type of activity result is coming back.
        private const val RC_SIGN_IN = 1
    }

    private val googleProvider = arrayListOf(
        AuthUI.IdpConfig.GoogleBuilder().build()
    )
    private val facebookProvider = arrayListOf(
        AuthUI.IdpConfig.FacebookBuilder().build()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setStatusBarTransparent()
        setupFirebaseDateConfig()

        if (FirebaseAuth.getInstance().currentUser != null)
            launchHomeActivity()

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

    /**
     * Launch the firebase authentication activity.
     * @param provider is an array containing all authentication types
     * the user can use to identify itself.
     * */
    private fun startSignin(provider: ArrayList<AuthUI.IdpConfig>) {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(provider)
                .build(),
            RC_SIGN_IN
        )
    }

    /**
     * Method called when firebase authentication is done.
     * If result is OK, we create (or update) the user account in the Firestore cloud.
     * Then we redirect the user to our home activity.
     * If result is CANCELED, we show an error message to the user according to its error type.
     * */
    private fun redirectAfterSignin(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    FirebaseAuth.getInstance().currentUser?.let {
                        createUser(
                            it.uid,
                            it.displayName,
                            it.email,
                            it.photoUrl.toString()
                        ).addOnFailureListener(super.onFailureListener())
                    }
                    launchHomeActivity()
                }
                Activity.RESULT_CANCELED -> {
                    val response = IdpResponse.fromResultIntent(data)
                    when {
                        response == null -> showCanceledSnackBar(R.string.login_canceled)
                        response.error?.errorCode == ErrorCodes.NO_NETWORK -> showCanceledSnackBar(R.string.login_no_network)
                        response.error?.errorCode == ErrorCodes.UNKNOWN_ERROR -> showCanceledSnackBar(R.string.login_unknown_error)
                    }
                }
            }
        }
    }

    private fun launchHomeActivity() {
        startActivity<HomeActivity>()
        this.finish()
    }

    private fun showCanceledSnackBar(errorMessage: Int) {
        Snackbar.make(loginActivityLayout, errorMessage, Snackbar.LENGTH_SHORT).show()
    }

    private fun setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
    }

    /**
     * Recommendation from firebase to fit with their
     * new implementation.
     * */
    private fun setupFirebaseDateConfig() {
        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        firestore.firestoreSettings = settings
    }
}