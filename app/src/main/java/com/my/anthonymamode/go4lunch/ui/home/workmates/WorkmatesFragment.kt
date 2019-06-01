package com.my.anthonymamode.go4lunch.ui.home.workmates

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
        val user = viewModel.currentUser
        workmatesRecyclerView.adapter =
            WorkmatesAdapter(generateOptionForAdapter(), user, WorkmateListType.ALL)
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