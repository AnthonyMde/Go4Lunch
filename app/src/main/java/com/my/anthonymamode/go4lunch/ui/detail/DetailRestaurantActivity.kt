package com.my.anthonymamode.go4lunch.ui.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.data.api.deleteFavoriteRestaurant
import com.my.anthonymamode.go4lunch.data.api.getCurrentUserData
import com.my.anthonymamode.go4lunch.data.api.getFavoriteRestaurant
import com.my.anthonymamode.go4lunch.data.api.getUsersByLunchId
import com.my.anthonymamode.go4lunch.data.api.setFavoriteRestaurant
import com.my.anthonymamode.go4lunch.data.api.updateUser
import com.my.anthonymamode.go4lunch.domain.Lunch
import com.my.anthonymamode.go4lunch.domain.PlaceDetail
import com.my.anthonymamode.go4lunch.domain.User
import com.my.anthonymamode.go4lunch.ui.LOCAL_USER_ID
import com.my.anthonymamode.go4lunch.ui.SHARED_PREFS
import com.my.anthonymamode.go4lunch.ui.chat.ChatActivity
import com.my.anthonymamode.go4lunch.ui.home.workmates.WorkmateListType
import com.my.anthonymamode.go4lunch.ui.home.workmates.WorkmatesAdapter
import com.my.anthonymamode.go4lunch.utils.Resource
import com.my.anthonymamode.go4lunch.utils.base.BaseActivity
import com.my.anthonymamode.go4lunch.utils.generateOptionForAdapter
import com.my.anthonymamode.go4lunch.utils.scaleDown
import com.my.anthonymamode.go4lunch.utils.scaleUp
import com.my.anthonymamode.go4lunch.utils.setStarsFormat
import kotlinx.android.synthetic.main.activity_detail_restaurant.detailRestaurantAddress
import kotlinx.android.synthetic.main.activity_detail_restaurant.detailRestaurantCallButton
import kotlinx.android.synthetic.main.activity_detail_restaurant.detailRestaurantFabDisable
import kotlinx.android.synthetic.main.activity_detail_restaurant.detailRestaurantFabEnable
import kotlinx.android.synthetic.main.activity_detail_restaurant.detailRestaurantLikeButton
import kotlinx.android.synthetic.main.activity_detail_restaurant.detailRestaurantName
import kotlinx.android.synthetic.main.activity_detail_restaurant.detailRestaurantPhoto
import kotlinx.android.synthetic.main.activity_detail_restaurant.detailRestaurantRating
import kotlinx.android.synthetic.main.activity_detail_restaurant.detailRestaurantRecyclerView
import kotlinx.android.synthetic.main.activity_detail_restaurant.detailRestaurantWebButton
import org.jetbrains.anko.toast

class DetailRestaurantActivity : BaseActivity() {
    private var isFavorite = false
    private var isLunchOfTheDay = false
    private var hasChangedFavoriteStatus = false
    private var hasChangedLunchOfDay = false
    private var user: User? = null
    private var mAdapter: FirestoreRecyclerAdapter<User, WorkmatesAdapter.WorkmatesViewHolder>? =
        null
    private lateinit var place: PlaceDetail
    private val viewModel by viewModels<DetailRestaurantViewModel>()
    private val userId by lazy {
        getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
            .getString(LOCAL_USER_ID, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_restaurant)

        val placeId = intent.getStringExtra("placeId")
        if (placeId == null) {
            toast("Sorry we can't retrieve these restaurant data for the moment")
            return
        }
        setObserver()
        viewModel.getPlaceDetail(placeId)
    }

    /**
     * Firestore adapter is updating automatically the data from the firestore database.
     * We stop listening here.
     * We only save the selected/unselected restaurant status we the user leaves the activity.
     */
    override fun onPause() {
        super.onPause()
        mAdapter?.stopListening()
        if (hasChangedFavoriteStatus) {
            updateFavorite()
            hasChangedFavoriteStatus = false
        }
        if (hasChangedLunchOfDay) {
            user?.let { updateUser(it) }
            hasChangedLunchOfDay = false
        }
    }

    /**
     * Firestore adapter is updating automatically the data from the firestore database.
     * We start listening here.
     */
    override fun onResume() {
        super.onResume()
        mAdapter?.startListening()
    }

    private fun setObserver() {
        /**
         * Configure all the activity (ui, cta, list, etc) only when we receive
         * the placeDetail object.
         */
        viewModel.placeDetail.observe(this, Observer {
            when (it) {
                is Resource.Loading -> toast("loading")
                is Resource.Success -> {
                    place = it.data
                    setRestaurantUI()
                    setCallToAction()
                    setCurrentUser()
                    configureRecyclerView()
                    detailRestaurantFabDisable.setOnClickListener { setLunchOfTheDay() }
                    detailRestaurantFabEnable.setOnClickListener { removeLunchOfTheDay() }
                }
                is Resource.Error -> {
                    toast("Sorry, we can't load this restaurant")
                    Log.e("NETWORK", "error : ${it.error}")
                }
            }
        })
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
        if (user?.lunch?.lunchOfTheDay == place.place_id) {
            isLunchOfTheDay = true
            detailRestaurantFabDisable.scaleDown()
            detailRestaurantFabEnable.scaleUp()
        }
    }

    private fun setRestaurantUI() {
        detailRestaurantName.text = place.name
        detailRestaurantAddress.text = place.formatted_address ?: place.address ?: ""
        setRating()
        setFavorite()
        detailRestaurantPhoto.setImageBitmap(place.photo)
    }

    /**
     * Set the adapter for the recycler and pass the data through it here
     */
    private fun configureRecyclerView() {
        val context = this
        mAdapter = WorkmatesAdapter(
            generateOptionForAdapter(getUsersByLunchId(place.place_id), this),
            userId,
            WorkmateListType.DETAIL,
            onChatIconClick = { workmateId, workmateName ->
                ChatActivity.navigateToChatActivity(workmateId, workmateName, context, context)
            }
        )
        detailRestaurantRecyclerView.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@DetailRestaurantActivity)
        }
    }

    private fun setCallToAction() {
        // PHONE CALL
        val phoneNumber = place.formatted_phone_number
        if (phoneNumber != null) {
            detailRestaurantCallButton.setOnClickListener {
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber")))
            }
        } else {
            detailRestaurantCallButton.setTextColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.lightGray,
                    null
                )
            )
            detailRestaurantCallButton.setCompoundDrawablesWithIntrinsicBounds(
                null,
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_local_phone_disable_color_24_dp,
                    null
                ),
                null,
                null
            )
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
        val website = place.website
        if (website != null) {
            detailRestaurantWebButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(website) }
                startActivity(intent)
            }
        } else {
            detailRestaurantWebButton.setTextColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.lightGray,
                    null
                )
            )
            detailRestaurantWebButton.setCompoundDrawablesWithIntrinsicBounds(
                null,
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_language_disable_color_24_dp,
                    null
                ),
                null,
                null
            )
        }
    }

    private fun setLunchOfTheDay() {
        isLunchOfTheDay = true
        hasChangedLunchOfDay = !hasChangedLunchOfDay
        detailRestaurantFabDisable.scaleDown()
        detailRestaurantFabEnable.scaleUp()
        user?.apply {
            hasLunch = true
            lunch = Lunch(place.place_id, place.name, place.address ?: place.formatted_address)
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
            getFavoriteRestaurant(uid, place.place_id).addOnSuccessListener {
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
            setFavoriteRestaurant(uid, place.place_id)
        } else {
            deleteFavoriteRestaurant(uid, place.place_id)
        }
    }

    private fun setStarColor(drawableId: Int, colorId: Int) {
        detailRestaurantLikeButton.setTextColor(ResourcesCompat.getColor(resources, colorId, null))
        val yellowStar = ResourcesCompat.getDrawable(resources, drawableId, null)
        detailRestaurantLikeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null,
            yellowStar,
            null,
            null
        )
    }

    private fun setRating() {
        val rating = setStarsFormat(googleRating = place.rating)
        if (rating < 0f) {
            detailRestaurantRating.visibility = INVISIBLE
        } else {
            detailRestaurantRating.visibility = VISIBLE
            detailRestaurantRating.rating = rating
        }
    }
}
