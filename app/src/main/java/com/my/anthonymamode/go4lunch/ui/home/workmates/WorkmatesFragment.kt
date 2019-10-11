package com.my.anthonymamode.go4lunch.ui.home.workmates

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.data.api.getUsersOrderedByLunch
import com.my.anthonymamode.go4lunch.domain.User
import com.my.anthonymamode.go4lunch.ui.chat.ChatActivity
import com.my.anthonymamode.go4lunch.ui.detail.DetailRestaurantActivity
import com.my.anthonymamode.go4lunch.ui.home.HomeViewModel
import com.my.anthonymamode.go4lunch.utils.base.BaseFragment
import com.my.anthonymamode.go4lunch.utils.generateOptionForAdapter
import kotlinx.android.synthetic.main.fragment_workmates.workmatesRecyclerView

class WorkmatesFragment : BaseFragment() {
    private var mAdapter: FirestoreRecyclerAdapter<User, WorkmatesAdapter.WorkmatesViewHolder>? = null

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

    override fun onPause() {
        super.onPause()
        mAdapter?.stopListening()
    }

    override fun onResume() {
        super.onResume()
        mAdapter?.startListening()
    }

    /**
     * Set the adapter for the recycler and pass the data through it here
     */
    private fun configureRecyclerView(activity: FragmentActivity) {
        val viewModel = ViewModelProviders.of(activity).get(HomeViewModel::class.java)
        val userId = viewModel.userId
        mAdapter = WorkmatesAdapter(
            generateOptionForAdapter(getUsersOrderedByLunch(), this),
            userId,
            WorkmateListType.ALL,
            onItemClick = { placeId ->
                val intent = Intent(context, DetailRestaurantActivity::class.java)
                intent.putExtra("placeId", placeId)
                startActivity(intent)
            },
            onChatIconClick = { workmateId, workmateName ->
                context?.let {
                    ChatActivity.navigateToChatActivity(
                        workmateId,
                        workmateName,
                        it
                    )
                }
            })
        workmatesRecyclerView.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
}
