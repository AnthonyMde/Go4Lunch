package com.my.anthonymamode.go4lunch

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.view.GravityCompat
import android.util.Log
import android.view.MenuItem
import androidx.lifecycle.Observer
import com.my.anthonymamode.go4lunch.R.id.*
import com.my.anthonymamode.go4lunch.utils.Resource
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.startActivity

class HomeActivity : BaseActivity() {
    private val viewModel: HomeViewModel by lazy {
        ViewModelProviders.of(this).get(HomeViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(homeToolbar)
        configureDrawerMenu()
        homeBottomNavBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        setObservers()
    }

    /**
     * Listen to click event on the BottomNavigationView items.
     * Each item redirects to a different app fragment.
     */
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

    /**
     * Set observers for the view which observes the live data of
     * its ViewModel.
     * For each live data observed, we can act according to its
     * current value.
     */
    private fun setObservers() {
        viewModel.logout.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    startActivity<LoginActivity>()
                    finish()
                }
                is Resource.Error -> {
                    showContent()
                    showToastError("Sorry, an error occurred")
                    Log.e("Logout Fail", it.error.message)
                }
            }
        })
    }

    /**
     * Set action to toolbar items.
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                homeDrawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    /**
     * Set menu and behavior of menu items in the navigation drawer.
     */
    private fun configureDrawerMenu() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_drawer_white_24dp)
        }
        homeNavigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                drawer_logout -> {
                    showLoader()
                    viewModel.logoutUser()
                }
                drawer_settings -> showMessage("Non implémenté")
                drawer_my_food -> showMessage("Non implémenté")
            }
            homeDrawerLayout.closeDrawers()
            true
        }
    }
}
