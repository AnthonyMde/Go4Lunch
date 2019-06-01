package com.my.anthonymamode.go4lunch.ui.home.workmates

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.domain.User
import kotlinx.android.synthetic.main.workmates_list_item.view.*

class WorkmatesAdapter(
    options: FirestoreRecyclerOptions<User>,
    /**
     * Connected user represented by its uid property
     */
    private val currentUser: String?,
    private val type: WorkmateListType,
    /**
     * The lambda needed by the ViewHolder to handle the click on each item
     */
    private val onClick: () -> Unit = {}
) :
    FirestoreRecyclerAdapter<User, WorkmatesAdapter.WorkmatesViewHolder>(options) {

    /**
     * Setup the layout for each item here.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkmatesViewHolder {
        return WorkmatesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.workmates_list_item,
                parent,
                false
            )
        )
    }

    /**
     * We can pass our data from the adapter to the ViewHolder here.
     * We filter the data to hide the item which is matching the current user.
     */
    override fun onBindViewHolder(holder: WorkmatesViewHolder, position: Int, data: User) {
        if (data.uid != currentUser)
            holder.bindDataToItemView(data)
        else
            holder.viewGone()
    }

    /**
     * WorkmatesFragment ViewHolder which manage everything relative to
     * a single item of the recycler view.
     */
    inner class WorkmatesViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {

        private var context: Context = view.context
        private lateinit var data: User

        /**
         * We bind all the data to the item view here.
         */
        fun bindDataToItemView(data: User) {
            this.data = data
            when (type) {
                WorkmateListType.ALL -> {
                    setDefaultStyle()
                }
                WorkmateListType.DETAIL -> {
                    setDetailStyle()
                }
            }
            setPhoto()
        }

        private fun setDetailStyle() {
            view.workmatesItemText.text =
                context.getString(R.string.detail_restaurant_workamtes_eating_there, data.displayName)
            view.workmatesItemText.setTypeface(null, Typeface.NORMAL)
            view.workmatesItemText.setTextColor(context.resources.getColor(android.R.color.black))
        }

        private fun setDefaultStyle() {
            if (data.hasLunch) {
                view.workmatesItemText.text = context.getString(R.string.workmates_has_lunch, data.displayName)
                view.workmatesItemText.setTypeface(null, Typeface.NORMAL)
                view.workmatesItemText.setTextColor(context.resources.getColor(android.R.color.black))
                view.setOnClickListener { onClick.invoke() }
            } else {
                view.workmatesItemText.text = context.getString(R.string.workmates_has_no_lunch, data.displayName)
                view.workmatesItemText.setTypeface(null, Typeface.ITALIC)
                view.workmatesItemText.setTextColor(context.resources.getColor(R.color.lightGray))
            }
        }

        private fun setPhoto() {
            Glide.with(context)
                .load(data.photoPath)
                .placeholder(R.drawable.profil_placeholder)
                .apply(RequestOptions.circleCropTransform())
                .into(view.workmatesItemPhoto)
        }

        /**
         * Used to remove the current user cell.
         */
        fun viewGone() {
            view.visibility = GONE
            view.layoutParams = RecyclerView.LayoutParams(0, 0)
        }
    }
}