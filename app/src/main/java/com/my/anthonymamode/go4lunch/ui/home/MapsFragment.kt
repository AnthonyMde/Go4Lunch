package com.my.anthonymamode.go4lunch.ui.home

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.domain.Place
import com.my.anthonymamode.go4lunch.domain.Places
import com.my.anthonymamode.go4lunch.utils.BaseFragment
import kotlinx.android.synthetic.main.fragment_maps.*
import org.jetbrains.anko.support.v4.longToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ZOOM_LEVEL = 16f
private const val MIN_ZOOM = 6f
private const val MAX_ZOOM = 20f

class MapsFragment : BaseFragment(), OnMapReadyCallback {
    private val viewModel by lazy {
        activity?.let {
            ViewModelProviders.of(it).get(HomeViewModel::class.java)
        }
    }

    private lateinit var mapsView: MapView
    private lateinit var mapsCenter: LatLng
    private var _googleMap: GoogleMap? = null
    private var lastTimePositionChanged = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_maps, container, false)
        mapsView = view.findViewById(R.id.mapsView)
        mapsView.getMapAsync(this)
        mapsView.onCreate(savedInstanceState)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapsFragmentRecenterFab.setOnClickListener {
            _googleMap?.apply {
                viewModel?.lastLocation?.value?.let {
                    moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            it,
                            ZOOM_LEVEL
                        )
                    )
                    mapsCenter = cameraPosition.target
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return
        with(googleMap) {
            _googleMap = this
            setMinZoomPreference(MIN_ZOOM)
            setMaxZoomPreference(MAX_ZOOM)
            setOnCameraIdleListener {
                mapsCenter = cameraPosition.target
                displayNearbyRestaurantWithDebounce()
            }
        }
        setLocation()
    }

    private fun displayNearbyRestaurantWithDebounce() {
        val debounceTime = 600L
        Handler().postDelayed({
            if (System.currentTimeMillis() - lastTimePositionChanged >= debounceTime) {
                displayNearbyRestaurant()
            }
        }, debounceTime)
        lastTimePositionChanged = System.currentTimeMillis()
    }

    private fun setLocation() {
        viewModel?.lastLocation?.observe(this, Observer {
            _googleMap?.apply {
                moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        it,
                        ZOOM_LEVEL
                    )
                )
                mapsCenter = it
                displayNearbyRestaurant()
            }
        })
    }

    private fun displayNearbyRestaurant() {
        val callback = object : Callback<Places> {
            override fun onFailure(call: Call<Places>, t: Throwable) {
                // TODO: set better error
                longToast("Impossible to get nearby restaurants : ${t.message}")
            }

            override fun onResponse(call: Call<Places>, response: Response<Places>) {
                if (response.isSuccessful) {
                    response.body()?.places?.let { setRestaurantMarkers(it) }
                }
            }
        }
        viewModel?.getRestaurantPlaces(mapsCenter, 1000)?.enqueue(callback)
    }

    private fun setRestaurantMarkers(restaurants: List<Place>) {
        _googleMap?.clear()
        for (restaurant in restaurants) {
            val latLng = LatLng(restaurant.geometry.location.lat, restaurant.geometry.location.lng)
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            _googleMap?.addMarker(markerOptions)
        }
    }

    override fun onResume() {
        mapsView?.onResume()
        super.onResume()
    }

    override fun onPause() {
        mapsView?.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapsView?.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        mapsView?.onLowMemory()
        super.onLowMemory()
    }
}