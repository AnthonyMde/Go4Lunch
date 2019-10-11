package com.my.anthonymamode.go4lunch.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.my.anthonymamode.go4lunch.BuildConfig
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.data.api.getCurrentUser
import com.my.anthonymamode.go4lunch.data.api.updateUser
import com.my.anthonymamode.go4lunch.ui.home.HomeActivity
import com.my.anthonymamode.go4lunch.utils.base.BaseActivity
import com.my.anthonymamode.go4lunch.utils.setStatusBarTransparent
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity

private const val RC_SIGN_IN = 1
const val SHARED_PREFS = BuildConfig.APPLICATION_ID
const val LOCAL_USER_ID = "USER_ID"

class LoginActivity : BaseActivity() {

    private val googleProvider = arrayListOf(
        AuthUI.IdpConfig.GoogleBuilder().build()
    )
    private val facebookProvider = arrayListOf(
        AuthUI.IdpConfig.FacebookBuilder().build()
    )
    private val hasLocationPermission: Boolean by lazy {
        val locationPermission =
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        locationPermission == PackageManager.PERMISSION_GRANTED
    }
    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.setStatusBarTransparent()
        setupFirebaseDateConfig()
        firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null)
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
     * If result is OK, we create (if it doesn't exist) the user account in the Firestore cloud.
     * Then we redirect the user to our home activity.
     * If result is CANCELED, we show an error message to the user according to its error type.
     * */
    private fun redirectAfterSignin(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    firebaseUser = FirebaseAuth.getInstance().currentUser
                    val user = firebaseUser
                        ?: return showToastError(getString(R.string.login_no_account_found_error))
                    getCurrentUser(user.uid).addOnSuccessListener {
                        val dataSize = it?.data?.size
                        if (data == null || dataSize == 0) {
                            updateUser(
                                user.uid,
                                user.displayName,
                                user.email,
                                user.photoUrl.toString()
                            ).addOnFailureListener(super.onFailureListener())
                        }
                        checkLocationPermissionAndRedirect()
                    }
                }
                Activity.RESULT_CANCELED -> {
                    val response = IdpResponse.fromResultIntent(data)
                    when {
                        response == null -> showCanceledSnackBar(R.string.login_canceled)
                        response.error?.errorCode == ErrorCodes.NO_NETWORK -> showCanceledSnackBar(R.string.login_no_network)
                        response.error?.errorCode == ErrorCodes.UNKNOWN_ERROR -> showCanceledSnackBar(
                            R.string.login_unknown_error
                        )
                    }
                }
            }
        }
    }

    private fun checkLocationPermissionAndRedirect() {
        if (!hasLocationPermission) {
            ifUidHasBeenSavedLaunchActivity<PermissionActivity>()
        } else {
            launchHomeActivity()
        }
    }

    private fun launchHomeActivity() {
        ifUidHasBeenSavedLaunchActivity<HomeActivity>()
    }

    private inline fun <reified T : Activity> ifUidHasBeenSavedLaunchActivity() {
        val hasBeenSaved = saveCurrentUserId(firebaseUser?.uid)
        if (hasBeenSaved) {
            startActivity<T>()
            this.finish()
        } else {
            alert(getString(R.string.save_user_id_failed))
        }
    }

    private fun saveCurrentUserId(uid: String?): Boolean {
        return getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(LOCAL_USER_ID, uid)
            .commit()
    }

    private fun showCanceledSnackBar(errorMessage: Int) {
        Snackbar.make(loginActivityLayout, errorMessage, Snackbar.LENGTH_SHORT).show()
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
