package com.my.anthonymamode.go4lunch.ui.home.list

import android.graphics.Bitmap
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.domain.Place
import com.my.anthonymamode.go4lunch.utils.toFormatDistance
import com.my.anthonymamode.go4lunch.utils.toStarsFormat
import kotlinx.android.synthetic.main.listitem_restaurant.view.*

class RestaurantAdapter(private val onClick: (Place) -> Unit) : RecyclerView.Adapter<RestaurantViewHolder>() {
    private var restaurantList = emptyList<Place>()
    private var restaurantPhoto = mutableMapOf<Int, Bitmap?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_restaurant, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
            holder.bindView(restaurantList[position], restaurantPhoto[position], onClick)
    }

    fun setRestaurantList(list: List<Place>, photos: MutableMap<Int, Bitmap?>) {
        restaurantList = list
        restaurantPhoto = photos
        notifyDataSetChanged()
    }
}

class RestaurantViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    fun bindView(restaurant: Place, restaurantPhoto: Bitmap?, onClick: (Place) -> Unit) {
        itemView.restaurantItemTitle.text = restaurant.name
        itemView.restaurantItemAddress.text = restaurant.address

        setPhoto(restaurant, restaurantPhoto)
        setRating(restaurant)
        setHowFarItIs(restaurant)
        setHours(restaurant)

        itemView.restaurantItemContainer.setOnClickListener {
            onClick(restaurant)
        }
    }

    private fun setHours(restaurant: Place) {
        val res = itemView.resources
        val hours = itemView.restaurantItemHours

        if (restaurant.opening_hours?.open_now == null) {
            hours.visibility = INVISIBLE
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
        val photo = bitmap ?: restaurant.icon
        val options = RequestOptions().apply {
            diskCacheStrategy(DiskCacheStrategy.ALL)
            skipMemoryCache(false)
        }
        Glide.with(itemView)
            .load(photo)
            .apply(options)
            .into(itemView.restaurantItemImage)
    }

    private fun setRating(restaurant: Place) {
        val rating = toStarsFormat(restaurant.rating)
        if (rating > 0) {
            itemView.restaurantItemRating.visibility = VISIBLE
            itemView.restaurantItemRating.rating = rating
        } else {
            itemView.restaurantItemRating.visibility = INVISIBLE
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