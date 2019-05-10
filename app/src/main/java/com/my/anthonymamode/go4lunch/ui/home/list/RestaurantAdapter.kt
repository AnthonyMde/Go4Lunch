package com.my.anthonymamode.go4lunch.ui.home.list

import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.domain.Place
import com.my.anthonymamode.go4lunch.utils.toFormatDistance
import kotlinx.android.synthetic.main.listitem_restaurant.view.*

class RestaurantAdapter : RecyclerView.Adapter<RestaurantViewHolder>() {
    private var restaurantList = emptyList<Place>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_restaurant, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        if (restaurantList.isNotEmpty()) {
            holder.bindView(restaurantList[position])
        }
    }

    fun setRestaurantList(list: List<Place>) {
        restaurantList = list
        notifyDataSetChanged()
    }
}

class RestaurantViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    // TODO: add a placeholder for restaurant image (glide)
    fun bindView(restaurant: Place) {
        itemView.restaurantItemTitle.text = restaurant.name
        itemView.restaurantItemAddress.text = restaurant.address

        setPhoto(restaurant)
        setRating(restaurant)
        setHowFarItIs(restaurant)
    }

    private fun setPhoto(restaurant: Place) {
        // TODO: display the right image for restaurant item
        val photo = restaurant.icon
        /*if (restaurant.photos != null && restaurant.photos.isNotEmpty()) {
            restaurant.photos[0].html_attributions[0]
        } else {
            restaurant.icon
        }*/
        Glide.with(itemView)
            .load(photo)
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

    private fun orderedPlacesByProximity(data: List<Place>): List<Place> {
        // TODO: return the list ordered by distance to display them in the right order in the list
        return emptyList()
    }
}