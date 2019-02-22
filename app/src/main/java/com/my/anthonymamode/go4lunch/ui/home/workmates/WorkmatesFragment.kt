package com.my.anthonymamode.go4lunch.ui.home.workmates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.api.getUsersOrderedByLunch
import com.my.anthonymamode.go4lunch.model.User
import com.my.anthonymamode.go4lunch.ui.home.HomeViewModel
import com.my.anthonymamode.go4lunch.utils.BaseFragment
import kotlinx.android.synthetic.main.fragment_workmates.*

class WorkmatesFragment : BaseFragment() {

    companion object {
        fun newInstance(): WorkmatesFragment {
            return WorkmatesFragment()
        }
    }

    private val workmatesQuery by lazy { getUsersOrderedByLunch() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workmates, container, false)
    }

    override fun onResume() {
        super.onResume()
        activity?.let { configureRecyclerView(it) }
    }

    private fun configureRecyclerView(activity: FragmentActivity) {
        val viewModel = ViewModelProviders.of(activity).get(HomeViewModel::class.java)
        val current = viewModel.currentUser
        workmatesRecyclerView.adapter =
            WorkmatesAdapter(generateOptionForAdapter(workmatesQuery), current)
        workmatesRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun generateOptionForAdapter(query: Query): FirestoreRecyclerOptions<User> {
        return FirestoreRecyclerOptions.Builder<User>()
            .setQuery(query, User::class.java)
            .setLifecycleOwner(this)
            .build()
    }
}