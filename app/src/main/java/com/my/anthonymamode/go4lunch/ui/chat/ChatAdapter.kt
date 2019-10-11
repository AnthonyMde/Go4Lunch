package com.my.anthonymamode.go4lunch.ui.chat

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.domain.Message
import com.my.anthonymamode.go4lunch.ui.LOCAL_USER_ID
import com.my.anthonymamode.go4lunch.ui.SHARED_PREFS
import kotlinx.android.synthetic.main.list_item_chat.view.chatMessageContent
import kotlinx.android.synthetic.main.list_item_chat.view.chatMessageDate
import org.jetbrains.anko.textColor

class ChatAdapter(
    options: FirestoreRecyclerOptions<Message>,
    context: Context
) : FirestoreRecyclerAdapter<Message, ChatAdapter.ChatViewHolder>(options) {
    val userId by lazy {
        context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
            .getString(LOCAL_USER_ID, null)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int, message: Message) {
        holder.bindUIElements(message)
    }

    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindUIElements(message: Message) {
            if (message.authorUid == userId) {
                setMessageGravity()
                setMessageStyle()
            }
            // TODO: display real Date
            /*SimpleDateFormat("HH:mm", Locale.FRANCE).run {
                itemView.chatMessageDate.text = format(message.dateCreated)
            }*/
            itemView.chatMessageDate.text = message.dateCreated
            itemView.chatMessageContent.text = message.content
        }

        private fun setMessageStyle() {
            itemView.chatMessageContent.apply {
                background = ResourcesCompat.getDrawable(context.resources, R.drawable.back_rounded_user_message, null)
                textColor = ResourcesCompat.getColor(context.resources, android.R.color.white, null)
            }
        }

        private fun setMessageGravity() {
            val params = itemView.chatMessageContent.layoutParams as LinearLayout.LayoutParams
            params.gravity = Gravity.END
            itemView.chatMessageContent.layoutParams = params
        }
    }
}
