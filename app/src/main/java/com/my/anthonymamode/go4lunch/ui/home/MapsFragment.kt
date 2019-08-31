package com.my.anthonymamode.go4lunch.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.ui.detail.DetailRestaurantActivity
import com.my.anthonymamode.go4lunch.utils.BaseFragment
import com.my.anthonymamode.go4lunch.utils.MapsHelper
import com.my.anthonymamode.go4lunch.utils.Resource
import com.my.anthonymamode.go4lunch.utils.debounceThatFunction
import kotlinx.android.synthetic.main.fragment_maps.*
import org.jetbrains.anko.support.v4.toast

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
    private var mapsHelper: MapsHelper? = null
    private var lastTimePositionChanged = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel?.lastLocation?.observe(this, Observer { position ->
            viewModel?.placeList?.observe(this, Observer {
                when (it) {
                    is Resource.Loading -> toast("loading")
                    is Resource.Success -> {
                        mapsHelper?.setRestaurantMarkers(it.data) { placeId ->
                            val intent = Intent(context, DetailRestaurantActivity::class.java)
                            intent.putExtra("placeId", placeId)
                            startActivity(intent)
                        }
                    }
                    is Resource.Error -> {
                        toast("We can't retrieve nearby restaurants")
                        Log.e("NETWORK", "error: ${it.error}")
                    }
                }
            })
            // TODO: uncomment to get nearby restaurants
            viewModel?.getRestaurantPlaces(position)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_maps, container, false)
        (activity as? HomeActivity)?.changeSearchVisibility(false)
        mapsView = view.findViewById(R.id.mapsView)
        mapsView.getMapAsync(this)
        mapsView.onCreate(savedInstanceState)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapsFragmentRecenterFab.setOnClickListener {
            viewModel?.lastLocation?.value?.let {
                mapsHelper?.centerMap(it, ZOOM_LEVEL)
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
                mapsHelper?.setMapsCenter(cameraPosition.target)
                lastTimePositionChanged =
                        // TODO: Delete this line and uncomment to displayRestaurant
                    // debounceThatFunction({}, 600L, lastTimePositionChanged)
                debounceThatFunction({ displayNearbyRestaurants() }, 600L, lastTimePositionChanged)
            }
        }
        setUserLocation()
    }

    private fun displayNearbyRestaurants() {
        val center = mapsHelper?.getMapsCenter()
        if (center != null) {
            viewModel?.getRestaurantPlaces(center)
        }
    }

    private fun setUserLocation() {
        viewModel?.lastLocation?.observe(this, Observer {
            mapsHelper?.centerMap(it, ZOOM_LEVEL)
            // TODO: uncomment to displayRestaurant
            displayNearbyRestaurants()
        })
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
