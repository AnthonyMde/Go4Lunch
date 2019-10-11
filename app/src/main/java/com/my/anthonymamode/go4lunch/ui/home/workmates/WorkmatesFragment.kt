package com.my.anthonymamode.go4lunch.ui.home.workmates

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.data.api.getUsersOrderedByLunch
import com.my.anthonymamode.go4lunch.domain.User
import com.my.anthonymamode.go4lunch.ui.ChatActivity
import com.my.anthonymamode.go4lunch.ui.detail.DetailRestaurantActivity
import com.my.anthonymamode.go4lunch.ui.home.HomeViewModel
import com.my.anthonymamode.go4lunch.utils.BaseFragment
import kotlinx.android.synthetic.main.fragment_workmates.*

class WorkmatesFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workmates, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let { configureRecyclerView(it) }
    }

    /**
     * Set the adapter for the recycler and pass the data through it here
     */
    private fun configureRecyclerView(activity: FragmentActivity) {
        val viewModel = ViewModelProviders.of(activity).get(HomeViewModel::class.java)
        val userId = viewModel.userId
        workmatesRecyclerView.adapter =
            WorkmatesAdapter(
                generateOptionForAdapter(),
                userId,
                WorkmateListType.ALL,
                onItemClick = { placeId ->
                    val intent = Intent(context, DetailRestaurantActivity::class.java)
                    intent.putExtra("placeId", placeId)
                    startActivity(intent)
                },
                onChatIconClick = { workmateId ->
                    context?.let { ChatActivity.navigateToChatActivity(workmateId, it) }
                })
        workmatesRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    /**
     * @return FirestoreRecyclerOptions of User which can be
     * directly used into a recycler view.
     */
    private fun generateOptionForAdapter(): FirestoreRecyclerOptions<User> {
        val query = getUsersOrderedByLunch()
        return FirestoreRecyclerOptions.Builder<User>()
            .setQuery(query, User::class.java)
            .setLifecycleOwner(this)
            .build()
    }
}
