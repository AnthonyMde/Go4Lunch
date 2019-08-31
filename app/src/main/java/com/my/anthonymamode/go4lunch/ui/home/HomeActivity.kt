package com.my.anthonymamode.go4lunch.ui.home

import android.Manifest
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.R.id.drawer_logout
import com.my.anthonymamode.go4lunch.R.id.drawer_my_food
import com.my.anthonymamode.go4lunch.R.id.drawer_settings
import com.my.anthonymamode.go4lunch.ui.LoginActivity
import com.my.anthonymamode.go4lunch.ui.PermissionActivity
import com.my.anthonymamode.go4lunch.ui.SettingsActivity
import com.my.anthonymamode.go4lunch.ui.home.list.RestaurantListFragment
import com.my.anthonymamode.go4lunch.ui.home.workmates.WorkmatesFragment
import com.my.anthonymamode.go4lunch.utils.BaseActivity
import com.my.anthonymamode.go4lunch.utils.Resource
import kotlinx.android.synthetic.main.activity_home.homeBottomNavBar
import kotlinx.android.synthetic.main.activity_home.homeDrawerLayout
import kotlinx.android.synthetic.main.activity_home.homeNavigationView
import kotlinx.android.synthetic.main.activity_home.homeToolbar
import kotlinx.android.synthetic.main.nav_drawer_header.view.navheaderEmail
import kotlinx.android.synthetic.main.nav_drawer_header.view.navheaderFullName
import kotlinx.android.synthetic.main.nav_drawer_header.view.navheaderProfileImage
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

private const val TLSE_LAT = 43.6043
private const val TLSE_LNG = 1.4437

class HomeActivity : BaseActivity() {
    // VARIABLES

    private val viewModel: HomeViewModel by lazy {
        ViewModelProviders.of(this).get(HomeViewModel::class.java)
    }

    private var isHide: Boolean = false

    private var searchView: SearchView? = null

    /**
     * @var fusedLocationClient client which allows to use the fused location API of google.
     * We use it to get the last known location and to get location updates.
     */
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // OVERRIDE FUNCTIONS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(homeToolbar)
        setObservers()
        homeBottomNavBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        configureDrawerMenu()
        viewModel.getUserInfo()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setLastLocation()
        supportFragmentManager.beginTransaction().add(
            R.id.contentView,
            MapsFragment()
        ).commit()
    }

    override fun onStart() {
        super.onStart()
        setLastLocation()
    }

    override fun onResume() {
        super.onResume()
        checkLocationPermission()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_bar_menu, menu)
        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = (menu?.findItem(R.id.search_toolbar)?.actionView as? SearchView)
        searchView?.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                toast("submit + $query")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                toast("$newText")
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.search_toolbar)?.isVisible = !isHide
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onNewIntent(intent: Intent?) {
        // Verify the action and get the query from the search voice input
        if (Intent.ACTION_SEARCH == intent?.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                searchView?.setQuery(query, true)
            }
        }

        super.onNewIntent(intent)
    }

    // PUBLIC FUNCTIONS

    fun changeSearchVisibility(hide: Boolean) {
        isHide = hide
        invalidateOptionsMenu()
    }

    // PRIVATE FUNCTIONS

    /**
     * Listen to click event on the BottomNavigationView items.
     * Each item redirects to a different app fragment.
     */
    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_map -> MapsFragment()
                R.id.navigation_list -> RestaurantListFragment()
                R.id.navigation_workmates -> WorkmatesFragment()
                else -> null
            }?.let {
                supportFragmentManager.beginTransaction().replace(R.id.contentView, it).commit()
                return@OnNavigationItemSelectedListener true
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
                        Glide.with(this)
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

    /**
     * Store the last known user lastLocation into the HomeViewModel.
     */
    private fun setLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { loc: Location? ->
                    loc?.let {
                        // TODO: add a better fallback if location is null
                        viewModel.setLastLocation(LatLng(it.latitude, it.longitude))
                    } ?: viewModel.setLastLocation(LatLng(TLSE_LAT, TLSE_LNG))
                }
        }
    }

    private fun redirectToLoginIfSessionExpired() {
        showToastError(getString(R.string.session_expired_error))
        startActivity<LoginActivity>()
        finish()
    }

    private fun checkLocationPermission() {
        val locationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= 23 && locationPermission != PackageManager.PERMISSION_GRANTED) {
            startActivity<PermissionActivity>()
            finish()
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
