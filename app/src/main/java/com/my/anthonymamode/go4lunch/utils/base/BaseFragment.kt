package com.my.anthonymamode.go4lunch.utils.base

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import com.my.anthonymamode.go4lunch.R
import org.jetbrains.anko.support.v4.find

abstract class BaseFragment : Fragment() {

    private val loadingView: View by lazy { find<View>(R.id.base_loader) }
    private val contentView: View by lazy { find<View>(R.id.contentView) }

    fun showContent() {
        contentView.visibility = VISIBLE
        loadingView.visibility = GONE
    }

    fun showLoading() {
        contentView.visibility = GONE
        loadingView.visibility = VISIBLE
    }
}
