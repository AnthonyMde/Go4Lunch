package com.my.anthonymamode.go4lunch.ui.home.list

import android.graphics.Bitmap
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.domain.Place
import com.my.anthonymamode.go4lunch.utils.toFormatDistance
import kotlinx.android.synthetic.main.listitem_restaurant.view.*

class RestaurantAdapter : RecyclerView.Adapter<RestaurantViewHolder>() {
    private var restaurantList = emptyList<Place>()
    private var restaurantPhoto: Array<Bitmap?>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_restaurant, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        if (position < restaurantPhoto?.size ?: position) {
            holder.bindView(restaurantList[position], restaurantPhoto?.get(position))
        }
    }

    fun setRestaurantList(list: List<Place>, photos: Array<Bitmap?>) {
        restaurantList = list
        restaurantPhoto = photos
        notifyDataSetChanged()
    }
}

class RestaurantViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    fun bindView(restaurant: Place, restaurantPhoto: Bitmap?) {
        itemView.restaurantItemTitle.text = restaurant.name
        itemView.restaurantItemAddress.text = restaurant.address

        setPhoto(restaurant, restaurantPhoto)
        setRating(restaurant)
        setHowFarItIs(restaurant)
        setHours(restaurant)
    }

    private fun setHours(restaurant: Place) {
        val res = itemView.resources
        val hours = itemView.restaurantItemHours

        if (restaurant.opening_hours?.open_now == null) {
            hours.visibility = GONE
            return
        }

        restaurant.opening_hours.open_now.let { open ->
            hours.visibility = VISIBLE
            if (open) {
                hours.text = res.getString(R.string.restaurant_item_is_opened)
                hours.setTextColor(res.getColor(R.color.lightGray))
            } else {
                hours.text = res.getString(R.string.restaurant_item_is_closed)
                hours.setTextColor(res.getColor(android.R.color.holo_red_light))
            }
        }
    }

    private fun setPhoto(restaurant: Place, bitmap: Bitmap?) {
        // TODO: use this value to get real API photos
        val photo = bitmap ?: restaurant.icon
        val options = RequestOptions().apply {
            diskCacheStrategy(DiskCacheStrategy.ALL)
            skipMemoryCache(false)
        }
        Glide.with(itemView)
            .load("https://picsum.photos/id/237/200/300")
            .apply(options)
            .into(itemView.restaurantItemImage)
    }

    private fun setRating(restaurant: Place) {
        if (restaurant.rating != null) {
            itemView.restaurantItemRating.visibility = VISIBLE
            when ((restaurant.rating * 0.6).toFloat()) { // From 5 stars rating to 3 stars rating
                in 0f..0.49f -> {
                    itemView.restaurantItemRating.visibility = GONE
                }
                in 0.5f..1f -> itemView.restaurantItemRating.rating = 1f
                in 1f..2f -> itemView.restaurantItemRating.rating = 2f
                in 2f..3f -> itemView.restaurantItemRating.rating = 3f
            }
        } else {
            itemView.restaurantItemRating.visibility = GONE
        }
    }

    private fun setHowFarItIs(restaurant: Place) {
        // TODO: get the real current location to compute the distances
        val currentLocation = Location("here").apply {
            latitude = 43.5746099
            longitude = 1.4524654
        }
        val restaurantLocation = Location("restaurant").apply {
            latitude = restaurant.geometry.location.lat
            longitude = restaurant.geometry.location.lng
        }
        val distance = currentLocation.distanceTo(restaurantLocation)
        itemView.restaurantItemDistance.text = distance.toFormatDistance()
    }
}