package com.my.anthonymamode.go4lunch.ui.home.workmates

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.model.User
import com.my.anthonymamode.go4lunch.utils.GlideApp
import kotlinx.android.synthetic.main.workmates_list_item.view.*

class WorkmatesAdapter(
    options: FirestoreRecyclerOptions<User>,
    private val currentUser: String?,
    private val listener: OnWorkmateClickListener
) :
    FirestoreRecyclerAdapter<User, WorkmatesViewHolder>(options) {
    interface OnWorkmateClickListener {
        fun onClick()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkmatesViewHolder {
        return WorkmatesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.workmates_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WorkmatesViewHolder, position: Int, data: User) {
        if (data.uid != currentUser)
            holder.bindDataToItemView(data)
        else
            holder.viewGone()
    }

    private fun onItemClicked() {
        listener.onClick()
    }
}

class WorkmatesViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    fun bindDataToItemView(data: User) {
        val context = view.context
        if (data.hasLunch) {
            view.workmatesItemText.text = context.getString(R.string.workamtes_has_lunch, data.displayName)
            view.workmatesItemText.setTypeface(null, Typeface.NORMAL)
            view.workmatesItemText.setTextColor(context.resources.getColor(android.R.color.black))
        } else {
            view.workmatesItemText.text = context.getString(R.string.workmates_has_no_lunch, data.displayName)
            view.workmatesItemText.setTypeface(null, Typeface.ITALIC)
            view.workmatesItemText.setTextColor(context.resources.getColor(R.color.lightGray))
        }

        GlideApp.with(context)
            .load(data.photoPath)
            .placeholder(R.drawable.profil_placeholder)
            .apply(RequestOptions.circleCropTransform())
            .into(view.workmatesItemPhoto)
    }

    fun viewGone() {
        view.visibility = GONE
        view.layoutParams = RecyclerView.LayoutParams(0, 0)
    }
}