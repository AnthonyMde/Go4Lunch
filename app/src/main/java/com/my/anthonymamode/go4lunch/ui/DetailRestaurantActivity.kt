package com.my.anthonymamode.go4lunch.ui

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.domain.Place
import com.my.anthonymamode.go4lunch.utils.BaseActivity
import com.my.anthonymamode.go4lunch.utils.toStarsFormat
import kotlinx.android.synthetic.main.activity_detail_restaurant.*

class DetailRestaurantActivity : BaseActivity() {
    private lateinit var place: Place

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_restaurant)
        place = intent.getSerializableExtra("place") as Place
        setRestaurantUI()
        // TODO: set coworkers
    }

    private fun setRestaurantUI() {
        detailRestaurantName.text = place.name
        val rating = toStarsFormat(place.rating)
        if (rating > 0) {
            detailRestaurantRating.visibility = VISIBLE
            detailRestaurantRating.rating = rating
        } else {
            detailRestaurantRating.visibility = GONE
        }
        // TODO: set image (make a new call with the photo ref)
    }
}
