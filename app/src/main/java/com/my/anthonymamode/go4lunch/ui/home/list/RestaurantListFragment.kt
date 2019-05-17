package com.my.anthonymamode.go4lunch.ui.home.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.domain.Place
import com.my.anthonymamode.go4lunch.domain.Places
import com.my.anthonymamode.go4lunch.ui.home.HomeViewModel
import com.my.anthonymamode.go4lunch.utils.BaseFragment
import kotlinx.android.synthetic.main.fragment_restaurant_list.*
import org.jetbrains.anko.support.v4.longToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantListFragment : BaseFragment() {
    private val viewModel by lazy {
        activity?.let {
            ViewModelProviders.of(it).get(HomeViewModel::class.java)
        }
    }

    private val restaurantAdapter = RestaurantAdapter()
    private val callback: Callback<Places> = getCallbackForPlaces()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel?.lastLocation?.observe(this, Observer { position ->
            viewModel?.getRestaurantPlaces(position)?.enqueue(callback)
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

    private fun getCallbackForPlaces(): Callback<Places> {
        return object : Callback<Places> {
            override fun onFailure(call: Call<Places>, t: Throwable) {
                // TODO: set better error
                longToast("Impossible to get nearby restaurants : ${t.message}")
            }

            override fun onResponse(call: Call<Places>, response: Response<Places>) {
                if (response.isSuccessful) {
                    response.body()?.places?.let {
                        configureRecyclerView(it)
                    }
                }
            }
        }
    }

    private fun configureRecyclerView(data: List<Place>) {
        val itemDecoration = DividerItemDecoration(
            requireContext(),
            DividerItemDecoration.VERTICAL
        )
        itemDecoration.setDrawable(requireContext().resources.getDrawable(R.drawable.rv_divider_no_padding))

        restaurantRV.addItemDecoration(itemDecoration)
        restaurantRV.adapter = restaurantAdapter
        restaurantRV.layoutManager = LinearLayoutManager(requireContext())
        restaurantAdapter.setRestaurantList(data)
    }
}
