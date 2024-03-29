package com.my.anthonymamode.go4lunch.ui.home.list

import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.model.LatLng
import com.my.anthonymamode.go4lunch.BuildConfig
import com.my.anthonymamode.go4lunch.BuildConfig.API_KEY_GOOGLE_PLACES
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.data.api.getUsersByLunchId
import com.my.anthonymamode.go4lunch.domain.Place
import com.my.anthonymamode.go4lunch.utils.distanceTo
import com.my.anthonymamode.go4lunch.utils.setStarsFormat
import com.my.anthonymamode.go4lunch.utils.toFormatDistance
import kotlinx.android.synthetic.main.list_item_restaurant.view.restaurantItemAddress
import kotlinx.android.synthetic.main.list_item_restaurant.view.restaurantItemContainer
import kotlinx.android.synthetic.main.list_item_restaurant.view.restaurantItemDistance
import kotlinx.android.synthetic.main.list_item_restaurant.view.restaurantItemHours
import kotlinx.android.synthetic.main.list_item_restaurant.view.restaurantItemImage
import kotlinx.android.synthetic.main.list_item_restaurant.view.restaurantItemRating
import kotlinx.android.synthetic.main.list_item_restaurant.view.restaurantItemTitle
import kotlinx.android.synthetic.main.list_item_restaurant.view.restaurantItemWorkmatesNumber
import kotlinx.android.synthetic.main.list_item_restaurant.view.restaurantItemWorkmatesNumberLayout
import java.util.Calendar

class RestaurantAdapter(
    private val userId: String?,
    private val localization: LatLng?,
    private val onItemClick: (String) -> Unit
) :
    RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    private var restaurantList = emptyList<Place>()
    // key == placeId and value == number of workmates
    private val workmateMap = mutableMapOf<String, Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_restaurant, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bindView(restaurantList[position])
    }

    fun setRestaurantList(list: List<Place>) {
        restaurantList = list
        notifyDataSetChanged()
    }

    inner class RestaurantViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val maxPhotoWidth = 1280
        // sunday == 0, saturday == 6
        private var currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1
        private var currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        private val currentMinute = Calendar.getInstance().get(Calendar.MINUTE)
        private lateinit var restaurant: Place

        fun bindView(place: Place) {
            restaurant = place
            itemView.restaurantItemTitle.text = restaurant.name
            itemView.restaurantItemAddress.text = restaurant.address

            setPhoto()
            setRating()
            setHowFarItIs()
            setOpeningHours()
            setWorkmateCounter()

            itemView.restaurantItemContainer.setOnClickListener {
                onItemClick(restaurant.place_id)
            }
        }

        /**
         * Set the next opening or closing text for the restaurant item
         */
        private fun setOpeningHours() {
            val hoursView = itemView.restaurantItemHours
            val open = restaurant.opening_hours?.open_now

            if (open == null) {
                hoursView.visibility = INVISIBLE
                return
            }

            restaurant.opening_hours?.let { openingHours ->
                val period =
                    getTheNextRestaurantHours(openingHours, currentDay, currentHour, currentMinute)
                val hoursText =
                    getRestaurantOpeningText(period, !open, currentDay, itemView.context)

                hoursView.text = hoursText
                hoursView.visibility = VISIBLE
            }
        }

        private fun setPhoto() {

            val photo = restaurant.photos?.get(0)?.photo_reference?.let {
                val query = "?key=$API_KEY_GOOGLE_PLACES&photoreference=$it&maxwidth=$maxPhotoWidth"
                "${BuildConfig.BASE_URL}photo$query"
            } ?: restaurant.icon

            val options = RequestOptions().apply {
                diskCacheStrategy(DiskCacheStrategy.ALL)
                skipMemoryCache(false)
            }

            Glide.with(itemView)
                .load(photo)
                .apply(options)
                .into(itemView.restaurantItemImage)
        }

        private fun setRating() {
            val rating = setStarsFormat(googleRating = restaurant.rating)
            if (rating < 0f) {
                itemView.restaurantItemRating.visibility = INVISIBLE
            } else {
                itemView.restaurantItemRating.visibility = VISIBLE
                itemView.restaurantItemRating.rating = rating
            }
        }

        /**
         * Set the distance between the user current location and the restaurant item
         */
        private fun setHowFarItIs() {
            val userLocation = localization ?: return
            val restaurantLoc = restaurant.geometry.location
            val distance = userLocation.distanceTo(restaurantLoc.lat, restaurantLoc.lng)
            itemView.restaurantItemDistance.text = distance.toFormatDistance()
        }

        /**
         * Set how many workmates have already chosen this restaurant item
         */
        private fun setWorkmateCounter() {
            itemView.restaurantItemWorkmatesNumberLayout.visibility = INVISIBLE
            val workmatesNumber = workmateMap[restaurant.place_id]
            if (workmatesNumber == null) {
                getWorkmatesNumber()
            } else {
                setCounterVisible(workmatesNumber)
            }
        }

        /**
         * If no workmate have already chosen this restaurant, we hide the counter
         */
        private fun setCounterVisible(workmatesNumber: Int) {
            itemView.restaurantItemWorkmatesNumber.text = itemView.context.getString(
                R.string.restaurant_item_workmates_number,
                workmatesNumber
            )
            itemView.restaurantItemWorkmatesNumberLayout.visibility = VISIBLE
        }

        private fun getWorkmatesNumber() {
            getUsersByLunchId(restaurant.place_id).get()
                .addOnSuccessListener {
                    var workmatesNumber = it.documents.size
                    val currentUser = it.documents.firstOrNull { doc ->
                        doc.data?.get("uid") == userId
                    }
                    currentUser?.let { workmatesNumber-- }
                    if (workmatesNumber > 0) {
                        setCounterVisible(workmatesNumber)
                        workmateMap[restaurant.place_id] = workmatesNumber
                    }
                }
        }
    }
}
