package com.my.anthonymamode.go4lunch.ui.detail

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.data.api.updateUser
import com.my.anthonymamode.go4lunch.data.api.deleteFavoriteRestaurant
import com.my.anthonymamode.go4lunch.data.api.getFavoriteRestaurant
import com.my.anthonymamode.go4lunch.data.api.setFavoriteRestaurant
import com.my.anthonymamode.go4lunch.data.api.getUsersByLunchId
import com.my.anthonymamode.go4lunch.data.api.getCurrentUserData
import com.my.anthonymamode.go4lunch.domain.Lunch
import com.my.anthonymamode.go4lunch.domain.Place
import com.my.anthonymamode.go4lunch.domain.User
import com.my.anthonymamode.go4lunch.ui.home.workmates.WorkmateListType
import com.my.anthonymamode.go4lunch.ui.home.workmates.WorkmatesAdapter
import com.my.anthonymamode.go4lunch.utils.BaseActivity
import com.my.anthonymamode.go4lunch.utils.scaleDown
import com.my.anthonymamode.go4lunch.utils.scaleUp
import com.my.anthonymamode.go4lunch.utils.toStarsFormat
import kotlinx.android.synthetic.main.activity_detail_restaurant.*
import okhttp3.ResponseBody
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Response

private const val MAX_PHOTO_WIDTH = 1280

class DetailRestaurantActivity : BaseActivity() {
    private var isFavorite = false
    private var isLunchOfTheDay = false
    private var hasChangedFavoriteStatus = false
    private var hasChangedLunchOfDay = false
    private var user: User? = null
    private var place: Place? = null
    private var placeId: String? = null
    private val viewModel by lazy { ViewModelProviders.of(this).get(DetailRestaurantViewModel::class.java) }
    private val userId by lazy { FirebaseAuth.getInstance().currentUser?.uid }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_restaurant)

        placeId = intent.getStringExtra("placeId")
        setRestaurantUI()
        setCallToAction()
        setCurrentUser()
        configureRecyclerView()
        detailRestaurantFabDisable.setOnClickListener { setLunchOfTheDay() }
        detailRestaurantFabEnable.setOnClickListener { removeLunchOfTheDay() }
    }

    override fun onPause() {
        super.onPause()
        if (hasChangedFavoriteStatus) {
            updateFavorite()
            hasChangedFavoriteStatus = false
        }
        if (hasChangedLunchOfDay) {
            user?.let { updateUser(it) }
            hasChangedLunchOfDay = false
        }
    }

    private fun setCurrentUser() {
        userId?.let { uid ->
            getCurrentUserData(uid).addOnSuccessListener { userData ->
                user = userData
                retrieveLunchChoice()
            }
        }
    }

    private fun retrieveLunchChoice() {
        if (user?.lunch?.lunchOfTheDay == place?.place_id ?: "") {
            isLunchOfTheDay = true
            detailRestaurantFabDisable.scaleDown()
            detailRestaurantFabEnable.scaleUp()
        }
    }

    private fun setRestaurantUI() {
        detailRestaurantName.text = place?.name ?: ""
        detailRestaurantAddress.text = place?.address ?: ""
        setRating()
        setFavorite()
        // TODO: uncomment to get real photo data (remind to add a placeholder if no photo found)
        // setRestaurantPhoto()
    }

    /**
     * Set the adapter for the recycler and pass the data through it here
     */
    private fun configureRecyclerView() {
        detailRestaurantRecyclerView.adapter =
            WorkmatesAdapter(generateOptionForAdapter(), userId, WorkmateListType.DETAIL)
        detailRestaurantRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    /**
     * @return FirestoreRecyclerOptions of User which can be
     * directly used into a recycler view.
     */
    private fun generateOptionForAdapter(): FirestoreRecyclerOptions<User> {
        val query = getUsersByLunchId(place?.place_id ?: "")
        return FirestoreRecyclerOptions.Builder<User>()
            .setQuery(query, User::class.java)
            .setLifecycleOwner(this)
            .build()
    }

    private fun setCallToAction() {
        // PHONE CALL
        detailRestaurantCallButton.setOnClickListener {
            // todo: add real phone number
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:0601020305")))
        }

        // LIKE BUTTON
        detailRestaurantLikeButton.setOnClickListener {
            hasChangedFavoriteStatus = !hasChangedFavoriteStatus
            isFavorite = !isFavorite
            if (isFavorite) {
                setStarColor(R.drawable.ic_star_color_yellow, R.color.yellowStar)
                toast(getString(R.string.detail_restaurant_toast_add_to_favorite))
            } else {
                setStarColor(R.drawable.ic_star_color_accent_24dp, R.color.colorPrimary)
                toast(getString(R.string.detail_restaurant_toast_remove_from_favorite))
            }
        }

        // WEB SITE BUTTON
        detailRestaurantWebButton.setOnClickListener {
            // TODO: add website feature
            toast("Should launch the website")
        }
    }

    private fun setLunchOfTheDay() {
        isLunchOfTheDay = true
        hasChangedLunchOfDay = !hasChangedLunchOfDay
        detailRestaurantFabDisable.scaleDown()
        detailRestaurantFabEnable.scaleUp()
        user?.apply {
            hasLunch = true
            lunch = Lunch(place?.place_id, place?.name)
        }
    }

    private fun removeLunchOfTheDay() {
        isLunchOfTheDay = false
        hasChangedLunchOfDay = !hasChangedLunchOfDay
        detailRestaurantFabEnable.scaleDown()
        detailRestaurantFabDisable.scaleUp()
        user?.apply {
            hasLunch = false
            lunch = null
        }
    }

    private fun setFavorite() {
        val uid = userId
        if (uid == null) {
            showToastError(getString(R.string.detail_restaurant_cannot_fetch_favorite_error))
        } else {
            getFavoriteRestaurant(uid, place?.place_id ?: "").addOnSuccessListener {
                if (it.data?.get("userId") != null) {
                    setStarColor(R.drawable.ic_star_color_yellow, R.color.yellowStar)
                    isFavorite = true
                }
            }
                .addOnFailureListener { super.onFailureListener() }
        }
    }

    private fun updateFavorite() {
        val uid = userId
            ?: return showToastError(getString(R.string.login_no_account_found_error))
        if (isFavorite) {
            setFavoriteRestaurant(uid, place?.place_id ?: "")
        } else {
            deleteFavoriteRestaurant(uid, place?.place_id ?: "")
        }
    }

    private fun setStarColor(drawableId: Int, colorId: Int) {
        detailRestaurantLikeButton.setTextColor(resources.getColor(colorId))
        val yellowStar = resources.getDrawable(drawableId)
        detailRestaurantLikeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, yellowStar, null, null)
    }

    private fun setRating() {
        val rating = toStarsFormat(place?.rating)
        if (rating > 0) {
            detailRestaurantRating.visibility = VISIBLE
            detailRestaurantRating.rating = rating
        } else {
            detailRestaurantRating.visibility = INVISIBLE
        }
    }

    private fun setRestaurantPhoto() {
        viewModel.getPlacePhoto(place?.photos?.get(0)?.photo_reference, MAX_PHOTO_WIDTH)
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
