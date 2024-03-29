package com.my.anthonymamode.go4lunch.ui.home.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.my.anthonymamode.go4lunch.BuildConfig
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.data.api.getPredictions
import com.my.anthonymamode.go4lunch.data.api.toRectangularBounds
import com.my.anthonymamode.go4lunch.domain.Place
import com.my.anthonymamode.go4lunch.ui.detail.DetailRestaurantActivity
import com.my.anthonymamode.go4lunch.ui.home.HomeViewModel
import com.my.anthonymamode.go4lunch.utils.base.BaseFragment
import com.my.anthonymamode.go4lunch.utils.Resource
import kotlinx.android.synthetic.main.fragment_restaurant_list.*
import org.jetbrains.anko.support.v4.longToast
import org.jetbrains.anko.support.v4.toast

const val radiusSearch = 700.0

class RestaurantListFragment : BaseFragment() {
    private val viewModel by activityViewModels<HomeViewModel>()

    private lateinit var restaurantAdapter: RestaurantAdapter

    private lateinit var placesClient: PlacesClient
    private var currentLocation: LatLng? = null
    private var listRestaurants: List<Place> = emptyList()
    private var searchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ctx = context ?: return
        Places.initialize(ctx.applicationContext, BuildConfig.API_KEY_GOOGLE_PLACES)
        placesClient = Places.createClient(ctx)

        restaurantAdapter = RestaurantAdapter(
            userId = viewModel.getUserId(),
            localization = viewModel.lastLocation.value,
            onItemClick = {
                val intent = Intent(context, DetailRestaurantActivity::class.java)
                intent.putExtra("placeId", it)
                startActivity(intent)
            })

        /**
         * Only when user last known location changes we update the list of restaurants
         */
        viewModel.lastLocation.observe(this, Observer { position ->
            if (currentLocation == position) {
                return@Observer
            }
            currentLocation = position
            viewModel.getRestaurantPlacesWithHours(position)
        })

        /**
         * Launch our restaurant list configuration when we receive a success from our viewmodel.
         */
        viewModel.placeWithHoursList.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }
                is Resource.Success -> {
                    listRestaurants = it.data
                    if (searchQuery == "") {
                        configureRecyclerView(listRestaurants)
                    }
                    showContent()
                }
                is Resource.Error -> {
                    toast("We can't retrieve nearby restaurants")
                    Log.e("NETWORK", "error: ${it.error}")
                }
            }
        })

        /**
         * Launch different restaurant query according to the user research
         */
        viewModel.searchPlaceQuery.observe(this, Observer { query ->
            if (searchQuery == query) {
                return@Observer
            }

            if (query == "") {
                restaurantAdapter.setRestaurantList(listRestaurants)
            } else {
                val bounds = toRectangularBounds(currentLocation, radiusSearch)
                val googleTask =
                    placesClient.findAutocompletePredictions(getPredictions(query, bounds))
                setResearchedRestaurantList(googleTask)
            }

            searchQuery = query
        })

        /**
         * Get the intersection between the api search result and our list of restaurants
         * to show only relevant restaurant results.
         */
        viewModel.searchPlaceList.observe(this, Observer { searchResults ->
            if (searchResults == null) {
                toast("No restaurant found for this research")
            } else {
                val matchingRestaurants = listRestaurants.filter { currentPlace ->
                    searchResults.map { it.placeId }.contains(currentPlace.place_id)
                }
                restaurantAdapter.setRestaurantList(matchingRestaurants)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant_list, container, false)
    }

    private fun configureRecyclerView(data: List<Place>) {
        val context = context ?: return
        val itemDecoration = DividerItemDecoration(
            context,
            DividerItemDecoration.VERTICAL
        )
        ResourcesCompat.getDrawable(resources, R.drawable.rv_divider_no_padding, null)?.let {
            itemDecoration.setDrawable(it)
        }

        contentView.addItemDecoration(itemDecoration)
        contentView.adapter = restaurantAdapter
        contentView.layoutManager = LinearLayoutManager(context)
        restaurantAdapter.setRestaurantList(data)
    }

    /**
     * Send in our viewmodel livedata the restaurant list received from the user search
     */
    private fun setResearchedRestaurantList(search: Task<FindAutocompletePredictionsResponse>?) {
        if (search == null) {
            longToast("No context or no localization found")
            return
        }

        search.addOnSuccessListener { response ->
            if (searchQuery == "") {
                return@addOnSuccessListener
            }
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
}
