package com.my.anthonymamode.go4lunch.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.my.anthonymamode.go4lunch.BuildConfig
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.data.api.getPredictions
import com.my.anthonymamode.go4lunch.domain.Place
import com.my.anthonymamode.go4lunch.ui.detail.DetailRestaurantActivity
import com.my.anthonymamode.go4lunch.utils.BaseFragment
import com.my.anthonymamode.go4lunch.utils.MapsHelper
import com.my.anthonymamode.go4lunch.utils.Resource
import com.my.anthonymamode.go4lunch.utils.debounceThatFunction
import kotlinx.android.synthetic.main.fragment_maps.*
import org.jetbrains.anko.support.v4.toast

private const val ZOOM_LEVEL = 14f
private const val MIN_ZOOM = 6f
private const val MAX_ZOOM = 20f

class MapsFragment : BaseFragment(), OnMapReadyCallback {
    private val viewModel by activityViewModels<HomeViewModel>()
    private lateinit var mapsView: MapView
    private lateinit var placesClient: PlacesClient
    private var currentLocation: LatLng? = null
    private var lastPositionOnMap: LatLng? = null
    private var mapsHelper: MapsHelper? = null
    private var placeList: List<Place>? = null
    private var searchQuery: String = ""
    private var lastTimePositionChanged: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ctx = context ?: return
        Places.initialize(ctx.applicationContext, BuildConfig.API_KEY_GOOGLE_PLACES)
        placesClient = Places.createClient(ctx)

        viewModel.lastLocation.observe(this, Observer { position ->
            if (currentLocation == position) {
                return@Observer
            }
            // do not comment this line because of SAM kotlin optimization it will crash with empty body
            viewModel.getRestaurantPlacesByRadius(position)
            currentLocation = position
        })

        viewModel.placeList.observe(this, Observer {
            when (it) {
                is Resource.Loading -> toast("loading")
                is Resource.Success -> {
                    placeList = it.data
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

        viewModel.searchPlaceQuery.observe(this, Observer { query ->
            // if nothing change, don't send the research
            if (searchQuery == query) {
                return@Observer
            }
            searchQuery = query
            // TODO: uncomment to get nearby restaurants
            // displayRestaurants(query)
        })

        viewModel.searchPlaceList.observe(this, Observer { searchResults ->
            if (searchResults == null) {
                toast("No restaurant found for this research")
            } else {
                mapsHelper?.displaySelectedRestaurants(searchResults, placeList) { placeId ->
                    val intent = Intent(context, DetailRestaurantActivity::class.java)
                    intent.putExtra("placeId", placeId)
                    startActivity(intent)
                }
            }
        })
    }

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
            viewModel.lastLocation.value?.let {
                mapsHelper?.centerMap(it, ZOOM_LEVEL)
                lastPositionOnMap = mapsHelper?.getMapsCenter()
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
                // TODO: uncomment to get nearby restaurants
                lastTimePositionChanged = debounceThatFunction(
                    { displayRestaurants(searchQuery) },
                    600L,
                    lastTimePositionChanged
                )
                lastPositionOnMap = mapsHelper?.getMapsCenter()
            }
        }
        setUserLocation()
    }

    private fun displayRestaurants(query: String = "") {
        if (query == "") {
            displayAllRestaurants()
        } else {
            val googleTask = getAutocompletePlaces(query)
            showSelectedRestaurants(googleTask)
        }
    }

    private fun displayAllRestaurants() {
        val center = mapsHelper?.getMapsCenter()
        if (center != null) {
            viewModel.getRestaurantPlacesByRadius(center)
        }
    }

    private fun setUserLocation() {
        viewModel.lastLocation.observe(this, Observer {
            val position = lastPositionOnMap ?: it
            mapsHelper?.centerMap(position, ZOOM_LEVEL)
        })
    }

    private fun getAutocompletePlaces(query: String): Task<FindAutocompletePredictionsResponse>? {
        val southWestBounds = mapsHelper?.getSouthWestBounds()
        val northEastBounds = mapsHelper?.getNorthEastBounds()

        if (southWestBounds == null || northEastBounds == null) {
            return null
        }

        val bounds = RectangularBounds.newInstance(southWestBounds, northEastBounds)

        return placesClient.findAutocompletePredictions(
            getPredictions(
                query,
                bounds
            )
        )
    }

    private fun showSelectedRestaurants(search: Task<FindAutocompletePredictionsResponse>?) {
        if (search == null) {
            showToastError("No context or no localization found")
            return
        }

        search.addOnSuccessListener { response ->
            viewModel.setSearchPlaces(response.autocompletePredictions)
        }
            .addOnFailureListener { exception ->
                if (exception is ApiException) {
                    Log.e("autocompletePredictions", "Place not found: " + exception.statusCode)
                } else {
                    Log.e("autocompletePredictions", "OtherError: " + exception.localizedMessage)
                }
            }
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
