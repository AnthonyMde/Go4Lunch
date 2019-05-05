package com.my.anthonymamode.go4lunch.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.domain.Places
import com.my.anthonymamode.go4lunch.utils.BaseFragment
import com.my.anthonymamode.go4lunch.utils.MapsHelper
import com.my.anthonymamode.go4lunch.utils.debounceThatFunction
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
    private lateinit var mapsHelper: MapsHelper
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
            viewModel?.lastLocation?.value?.let {
                mapsHelper.centerMap(it, ZOOM_LEVEL)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mapsHelper = MapsHelper(googleMap)
        googleMap ?: return
        with(googleMap) {
            setMinZoomPreference(MIN_ZOOM)
            setMaxZoomPreference(MAX_ZOOM)
            setOnCameraIdleListener {
                mapsHelper.setMapsCenter(cameraPosition.target)
                lastTimePositionChanged =
                    debounceThatFunction({ displayNearbyRestaurant() }, 600L, lastTimePositionChanged)
            }
        }
        getUserLocation()?.let {
            mapsHelper.centerMap(it, ZOOM_LEVEL)
            displayNearbyRestaurant()
        }
    }

    private fun getUserLocation(): LatLng? {
        viewModel?.lastLocation?.let {
            return it.value
        } ?: return null // TODO: force the location
    }

    private fun displayNearbyRestaurant() {
        val callback = object : Callback<Places> {
            override fun onFailure(call: Call<Places>, t: Throwable) {
                // TODO: set better error
                longToast("Impossible to get nearby restaurants : ${t.message}")
            }

            override fun onResponse(call: Call<Places>, response: Response<Places>) {
                if (response.isSuccessful) {
                    response.body()?.places?.let { mapsHelper.setRestaurantMarkers(it) }
                }
            }
        }
        val center = mapsHelper.getMapsCenter()
        if (center != null)
            viewModel?.getRestaurantPlaces(center, 1000)?.enqueue(callback)
    }

    override fun onResume() {
        mapsView.onResume()
        super.onResume()
    }

    override fun onPause() {
        mapsView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapsView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        mapsView.onLowMemory()
        super.onLowMemory()
    }
}