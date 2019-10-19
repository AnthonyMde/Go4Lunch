package com.my.anthonymamode.go4lunch.ui.home.list

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
import com.google.android.gms.maps.model.LatLng
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.data.api.getUsersByLunchId
import com.my.anthonymamode.go4lunch.domain.Hours
import com.my.anthonymamode.go4lunch.domain.Period
import com.my.anthonymamode.go4lunch.domain.Place
import com.my.anthonymamode.go4lunch.utils.toFormatDistance
import com.my.anthonymamode.go4lunch.utils.toStarsFormat
import kotlinx.android.synthetic.main.list_item_restaurant.view.*
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
        private val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1
        private val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        private val currentMinute = Calendar.getInstance().get(Calendar.MINUTE)
        private lateinit var restaurant: Place

        fun bindView(place: Place) {
            restaurant = place
            itemView.restaurantItemTitle.text = restaurant.name
            itemView.restaurantItemAddress.text = restaurant.address

            setPhoto()
            setRating()
            setHowFarItIs()
            setHours()
            getWorkmateCounter()

            itemView.restaurantItemContainer.setOnClickListener {
                onItemClick(restaurant.place_id)
            }
        }

        private fun setHours() {
            val hoursView = itemView.restaurantItemHours
            val open = restaurant.opening_hours?.open_now

            if (open == null) {
                hoursView.visibility = INVISIBLE
                return
            }

            restaurant.opening_hours?.let {
                val period = getTheNextRestaurantHours(it)
                val hoursText = getRestaurantOpeningText(period, !open)

                hoursView.text = hoursText
                hoursView.visibility = VISIBLE
            }
        }

        private fun setPhoto() {

            val photo: String? = null/*restaurant.photos?.get(0)?.photo_reference?.let {
            val query = "?key=$API_KEY_GOOGLE_PLACES&photoreference=$it&maxwidth=$maxPhotoWidth"
            "${BuildConfig.BASE_URL}photo$query"
        } ?: restaurant.icon*/

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
            val rating = restaurant.rating?.toStarsFormat()
            if (rating == null || rating < 0) {
                itemView.restaurantItemRating.visibility = INVISIBLE
            } else {
                itemView.restaurantItemRating.visibility = VISIBLE
                itemView.restaurantItemRating.rating = rating
            }
        }

        private fun getWorkmateCounter() {
            itemView.restaurantItemWorkmatesNumberLayout.visibility = INVISIBLE
            val workmatesNumber = workmateMap[restaurant.place_id]
            if (workmatesNumber == null) {
                getWorkmatesNumber()
            } else {
                setCounterVisible(workmatesNumber)
            }
        }

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

        private fun setHowFarItIs() {
            val userLocation = localization ?: return
            val currentLocation = Location("here").apply {
                latitude = userLocation.latitude
                longitude = userLocation.longitude
            }
            val restaurantLocation = Location("restaurant").apply {
                latitude = restaurant.geometry.location.lat
                longitude = restaurant.geometry.location.lng
            }
            val distance = currentLocation.distanceTo(restaurantLocation)
            itemView.restaurantItemDistance.text = distance.toFormatDistance()
        }

        fun getTheNextRestaurantHours(hours: Hours): Period? {
            val periods = hours.periods

            // Sorted by smaller time interval from current day
            // todo: work only when json is right formatted (period with logic sort).
            val sortedPeriods = periods.sortedWith(
                compareBy {
                    val targetTime = if (hours.open_now) it.open else it.close
                    getHoursInterval(
                        currentDay,
                        getCurrentHourWithMinutes(),
                        targetTime.day,
                        Integer.parseInt(targetTime.time)
                    )
                }
            )
            return sortedPeriods.firstOrNull()
        }

        private fun getHoursInterval(
            currentDay: Int,
            currentHour: Int,
            targetDay: Int,
            targetHour: Int
        ): Int {
            val sortedDays = (currentDay..6) + (0 until currentDay)
            val hoursInDay = 24
            val daysInWeek = 7
            val minutesInHour = 60
            // specific computation if target day equal current day but is in the past
            return if (currentDay == targetDay && currentHour > targetHour) {
                hoursInDay * daysInWeek * minutesInHour - (currentHour - targetHour)
                // standard computation to return the hour interval in format 'HHMM'
            } else {
                hoursInDay * sortedDays.indexOf(targetDay) * minutesInHour + (targetHour - currentHour)
            }
        }

        private fun getCurrentHourWithMinutes(): Int {
            val stringHourWithMinutes = if (currentMinute < 10) {
                (currentHour * 10).toString() + currentMinute.toString()
            } else {
                currentHour.toString() + currentMinute.toString()
            }

            return Integer.parseInt(stringHourWithMinutes)
        }

        fun getRestaurantOpeningText(period: Period?, forOpening: Boolean): String {
            if (period == null) {
                return if (forOpening) "permanently closed" else "opened 24/7"
            }

            val targetTime = if (forOpening) period.open else period.close
            val dayText = if (targetTime.day == currentDay) "" else getDay(targetTime.day)
            val timeText = targetTime.time

            return StringBuilder()
                .append(if (forOpening) "opens " else "closes ")
                .append(if (dayText.isNotEmpty()) "$dayText " else "")
                .append("at $timeText")
                .toString()
        }

        private fun getDay(dayNumber: Int): String {
            return when (dayNumber) {
                0 -> "dimanche"
                1 -> "lundi"
                2 -> "mardi"
                3 -> "mercredi"
                4 -> "jeudi"
                5 -> "vendredi"
                6 -> "samedi"
                else -> "prochainement"
            }
        }
    }
}
