package com.my.anthonymamode.go4lunch.ui.home

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.R.id.drawer_logout
import com.my.anthonymamode.go4lunch.R.id.drawer_my_food
import com.my.anthonymamode.go4lunch.R.id.drawer_settings
import com.my.anthonymamode.go4lunch.ui.LoginActivity
import com.my.anthonymamode.go4lunch.ui.SettingsActivity
import com.my.anthonymamode.go4lunch.ui.home.workmates.WorkmatesFragment
import com.my.anthonymamode.go4lunch.utils.BaseActivity
import com.my.anthonymamode.go4lunch.utils.GlideApp
import com.my.anthonymamode.go4lunch.utils.Resource
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.nav_drawer_header.view.*
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
        supportFragmentManager.beginTransaction().add(
            R.id.contentView,
            MapsFragment.newInstance()
        ).commit()

        setObservers()
        viewModel.getUserInfo()
    }

    /**
     * Listen to click event on the BottomNavigationView items.
     * Each item redirects to a different app fragment.
     */
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_map -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.contentView,
                    MapsFragment.newInstance()
                ).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_list -> {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.contentView,
                        RestaurantListFragment.newInstance()
                    ).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_workmates -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.contentView,
                    WorkmatesFragment.newInstance()
                )
                    .commit()
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
                    showToastError(getString(R.string.logout_error))
                    Log.e("Firebase error", "Can't log out user : ${it.error.message}")
                }
            }
        })
        viewModel.userInfo.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    it.data?.let { userData ->
                        val header = homeNavigationView.getHeaderView(0)
                        header.navheaderEmail.text = userData.email
                        header.navheaderFullName.text = userData.displayName
                        GlideApp.with(this)
                            .load(userData.photoUrl)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .placeholder(R.drawable.profil_placeholder)
                            .apply(RequestOptions.circleCropTransform())
                            .into(header.navheaderProfileImage)
                    } ?: redirectToLoginIfSessionExpired()
                }
                is Resource.Error -> {
                    redirectToLoginIfSessionExpired()
                }
            }
        })
    }

    private fun redirectToLoginIfSessionExpired() {
        showToastError(getString(R.string.session_expired_error))
        startActivity<LoginActivity>()
        finish()
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
                drawer_settings -> startActivity<SettingsActivity>()
                drawer_my_food -> showMessage("Non implémenté")
            }
            homeDrawerLayout.closeDrawers()
            true
        }
    }
}
