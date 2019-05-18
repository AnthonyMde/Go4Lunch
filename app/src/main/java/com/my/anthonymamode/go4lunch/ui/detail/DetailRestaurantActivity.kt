package com.my.anthonymamode.go4lunch.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.domain.Place
import com.my.anthonymamode.go4lunch.utils.BaseActivity
import com.my.anthonymamode.go4lunch.utils.toStarsFormat
import kotlinx.android.synthetic.main.activity_detail_restaurant.*
import org.jetbrains.anko.toast

class DetailRestaurantActivity : BaseActivity() {
    private lateinit var place: Place
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_restaurant)
        place = intent.getSerializableExtra("place") as Place
        // TODO: retrieve from firebase if it is favorite or not
        setRestaurantUI()
        setCallToAction()
        // TODO: set coworkers
    }

    override fun onPause() {
        super.onPause()
        // todo : store to firebase only in the onPause : to avoid multiple useless calls
    }

    private fun setCallToAction() {
        detailRestaurantCallButton.setOnClickListener {
            // todo: add real phone number
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:0601020305")))
        }
        detailRestaurantLikeButton.setOnClickListener {
            isFavorite = !isFavorite
            if (isFavorite) {
                setStarColor(R.drawable.ic_star_color_yellow, R.color.yellowStar)
            } else {
                setStarColor(R.drawable.ic_star_color_accent_24dp, R.color.colorPrimary)
            }
        }
        detailRestaurantWebButton.setOnClickListener {
            toast("Should launch the website")
        }
    }

    private fun setStarColor(drawableId: Int, colorId: Int) {
        detailRestaurantLikeButton.setTextColor(resources.getColor(colorId))
        val yellowStar = resources.getDrawable(drawableId)
        detailRestaurantLikeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, yellowStar, null, null)
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
