package com.my.anthonymamode.go4lunch.ui

import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_POSITIVE
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.api.deleteUser
import com.my.anthonymamode.go4lunch.utils.BaseActivity
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.startActivity

class SettingsActivity : BaseActivity() {
    private val userInfo by lazy {
        FirebaseAuth.getInstance().currentUser
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        configureToolbar()
        setListeners()
    }

    private fun setListeners() {
        deleteAccountButton.setOnClickListener {
            val uid = userInfo?.uid
            if (uid != null) {
                deleteConfirmation(uid)
            } else {
                showToastError(getString(R.string.settings_delete_account_error))
                redirectToLogin()
            }
        }
    }

    private fun deleteConfirmation(uid: String) {
        AlertDialog.Builder(this, R.style.AlertDialogCustom).create()
            .apply {
                setMessage(getString(R.string.settings_delete_account_alert_dialog_message))
                setTitle(getString(R.string.settings_delete_account_alert_dialog_title))
                setButton(
                    BUTTON_POSITIVE,
                    getString(R.string.settings_delete_account_alert_dialog_positive_button)
                ) { _, _ ->
                    showLoader()
                    deleteUser(uid)
                        .addOnFailureListener(super.onFailureListener())
                        .addOnSuccessListener(onSuccessListener())
                }
                setButton(
                    BUTTON_NEGATIVE,
                    getString(R.string.settings_delete_account_alert_dialog_negative_button)
                ) { dialog, _ -> dialog.dismiss() }
                this.show()
                getButton(BUTTON_POSITIVE)?.background =
                    resources.getDrawable(R.drawable.back_rounded_delete_account_button)
                getButton(BUTTON_POSITIVE)?.setTextColor(resources.getColor(android.R.color.black))
                this.show()
            }
    }

    private fun redirectToLogin() {
        startActivity<LoginActivity>()
        finish()
    }

    private fun onSuccessListener(): OnSuccessListener<Void> {
        return OnSuccessListener {
            AuthUI.getInstance().delete(this).addOnSuccessListener {
                showContent()
                showMessage(getString(R.string.settings_delete_account_success_toast))
                redirectToLogin()
            }.addOnFailureListener {
                showContent()
                showMessage(getString(R.string.settings_delete_account_success_but_no_logout))
            }
        }
    }

    private fun configureToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
