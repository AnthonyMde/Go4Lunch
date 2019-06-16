package com.my.anthonymamode.go4lunch.ui.home.list

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.data.api.entity.PlaceResponseWrapper
import com.my.anthonymamode.go4lunch.domain.Place
import com.my.anthonymamode.go4lunch.ui.detail.DetailRestaurantActivity
import com.my.anthonymamode.go4lunch.ui.home.HomeViewModel
import com.my.anthonymamode.go4lunch.utils.BaseFragment
import kotlinx.android.synthetic.main.fragment_restaurant_list.*
import okhttp3.ResponseBody
import org.jetbrains.anko.support.v4.longToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val MAX_PHOTO_WIDTH = 300

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
            viewModel?.getRestaurantPlaces(position)?.enqueue(getCallbackForPlaces())
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

    private fun configureRecyclerView(data: List<Place>, photos: MutableMap<Int, Bitmap?>) {
        val context = context ?: return
        val itemDecoration = DividerItemDecoration(
            context,
            DividerItemDecoration.VERTICAL
        )
        itemDecoration.setDrawable(context.resources.getDrawable(R.drawable.rv_divider_no_padding))

        restaurantRV.addItemDecoration(itemDecoration)
        restaurantRV.adapter = restaurantAdapter
        restaurantRV.layoutManager = LinearLayoutManager(context)
        restaurantAdapter.setRestaurantList(data, photos)
    }

    private fun getCallbackForPlaces(): Callback<PlaceResponseWrapper> {
        return object : Callback<PlaceResponseWrapper> {
            override fun onFailure(call: Call<PlaceResponseWrapper>, t: Throwable) {
                // TODO: set better error
                longToast("Impossible to get nearby restaurants : ${t.message}")
            }

            override fun onResponse(call: Call<PlaceResponseWrapper>, response: Response<PlaceResponseWrapper>) {
                if (response.isSuccessful) {
                    response.body()?.places?.let { places ->
                        // TODO: remove this line and uncomment to get real placeDetail photo
                        configureRecyclerView(places, mutableMapOf())
                        /*val placesPhoto = mutableMapOf<Int, Bitmap?>()
                        places.forEachIndexed { index, placeDetail ->
                            // Call the google photo API to get each placeDetail photo
                            viewModel?.getPlacePhoto(placeDetail.photos?.get(0)?.photo_reference, MAX_PHOTO_WIDTH)
                                ?.enqueue(getCallbackForPhoto(placesPhoto, places, index))
                        }*/
                    }
                }
            }
        }
    }

    private fun getCallbackForPhoto(
        placesPhoto: MutableMap<Int, Bitmap?>,
        places: List<Place>,
        index: Int
    ): Callback<ResponseBody> {
        return object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val bitmap = BitmapFactory.decodeStream(response.body()?.byteStream())
                    placesPhoto[index] = bitmap
                    if (places.size == placesPhoto.size) {
                        configureRecyclerView(places, placesPhoto)
                    }
                } else {
                    placesPhoto[index] = null

                    if (places.size == placesPhoto.size) {
                        configureRecyclerView(places, placesPhoto)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                placesPhoto[index] = null

                if (places.size == placesPhoto.size) {
                    configureRecyclerView(places, placesPhoto)
                }
            }
        }
    }
}
