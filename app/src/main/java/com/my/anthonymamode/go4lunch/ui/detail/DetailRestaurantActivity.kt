package com.my.anthonymamode.go4lunch.ui.detail

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.lifecycle.ViewModelProviders
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.domain.Place
import com.my.anthonymamode.go4lunch.utils.BaseActivity
import com.my.anthonymamode.go4lunch.utils.setStatusBarTransparent
import com.my.anthonymamode.go4lunch.utils.toStarsFormat
import kotlinx.android.synthetic.main.activity_detail_restaurant.*
import okhttp3.ResponseBody
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Response

private const val MAX_PHOTO_WIDTH = 1280

class DetailRestaurantActivity : BaseActivity() {
    private lateinit var place: Place
    private var isFavorite: Boolean = false
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(DetailRestaurantViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_restaurant)
        window.setStatusBarTransparent()
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
                toast(getString(R.string.detail_restaurant_toast_add_to_favorite))
            } else {
                setStarColor(R.drawable.ic_star_color_accent_24dp, R.color.colorPrimary)
                toast(getString(R.string.detail_restaurant_toast_remove_from_favorite))
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
        detailRestaurantAddress.text = place.address ?: ""
        setRating()
        // TODO: uncomment to get real photo data (remind to add a placeholder if no photo found)
        // setRestaurantPhoto()
    }

    private fun setRating() {
        val rating = toStarsFormat(place.rating)
        if (rating > 0) {
            detailRestaurantRating.visibility = VISIBLE
            detailRestaurantRating.rating = rating
        } else {
            detailRestaurantRating.visibility = INVISIBLE
        }
    }

    private fun setRestaurantPhoto() {
        viewModel.getPlacePhoto(place.photos?.get(0)?.photo_reference, MAX_PHOTO_WIDTH)
            .enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("NETWORK", "Fail to get detail restaurant photo : ${t.message}")
                }

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val bitmap = BitmapFactory.decodeStream(response.body()?.byteStream())
                        detailRestaurantPhoto.setImageBitmap(bitmap)
                    }
                }
            })
    }
}
