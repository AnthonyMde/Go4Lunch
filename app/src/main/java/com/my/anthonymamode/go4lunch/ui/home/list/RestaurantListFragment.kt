package com.my.anthonymamode.go4lunch.ui.home.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.domain.Place
import com.my.anthonymamode.go4lunch.ui.detail.DetailRestaurantActivity
import com.my.anthonymamode.go4lunch.ui.home.HomeViewModel
import com.my.anthonymamode.go4lunch.utils.BaseFragment
import com.my.anthonymamode.go4lunch.utils.Resource
import kotlinx.android.synthetic.main.fragment_restaurant_list.*
import org.jetbrains.anko.support.v4.toast

class RestaurantListFragment : BaseFragment() {
    private val viewModel by lazy {
        activity?.let {
            ViewModelProviders.of(it).get(HomeViewModel::class.java)
        }
    }

    private val restaurantAdapter = RestaurantAdapter(onClick = {
        val intent = Intent(context, DetailRestaurantActivity::class.java)
        intent.putExtra("placeId", it)
        startActivity(intent)
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel?.lastLocation?.observe(this, Observer { position ->
            viewModel?.placeWithHoursList?.observe(this, Observer {
                when (it) {
                    is Resource.Loading -> toast("loading")
                    is Resource.Success -> {
                        configureRecyclerView(it.data)
                    }
                    is Resource.Error -> {
                        toast("We can't retrieve nearby restaurants")
                        Log.e("NETWORK", "error: ${it.error}")
                    }
                }
            })
            viewModel?.getRestaurantPlacesWithHours(position)
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
        itemDecoration.setDrawable(context.resources.getDrawable(R.drawable.rv_divider_no_padding))

        restaurantRV.addItemDecoration(itemDecoration)
        restaurantRV.adapter = restaurantAdapter
        restaurantRV.layoutManager = LinearLayoutManager(context)
        restaurantAdapter.setRestaurantList(data)
    }
}
