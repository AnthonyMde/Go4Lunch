package com.my.anthonymamode.go4lunch

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.GravityCompat
import android.util.Log
import android.view.MenuItem
import com.firebase.ui.auth.AuthUI
import com.my.anthonymamode.go4lunch.R.id.*
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(homeToolbar)
        configureDrawerMenu()
        homeBottomNavBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_map -> {
                // TODO: fragment transaction (map fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_list -> {
                // TODO: fragment transaction (list fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_workmates -> {
                // TODO: fragment transaction (workmates fragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                homeDrawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun configureDrawerMenu() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_drawer_white_24dp)
        }
        homeNavigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                drawer_logout -> {
                    logout()
                }
                drawer_settings -> showMessage("Non implémenté")
                drawer_my_food -> showMessage("Non implémenté")
            }
            homeDrawerLayout.closeDrawers()
            true
        }
    }

    private fun logout() {
        showLoader()
        AuthUI.getInstance()
            .signOut(this)
            .addOnSuccessListener {
                redirectToLogin()
                finish()
            }
            .addOnFailureListener {
                showContent()
                showToastError("Sorry, an error occurred")
                Log.e("Logout Fail", it.toString())
            }
    }

    private fun redirectToLogin() {
        intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_TASK_ON_HOME
        startActivity(intent)
    }
}
