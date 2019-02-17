package com.my.anthonymamode.go4lunch.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.utils.BaseFragment

class WorkmatesFragment : BaseFragment() {

    companion object {
        fun newInstance(): WorkmatesFragment {
            return WorkmatesFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workmates, container, false)
    }
}