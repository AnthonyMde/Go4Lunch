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
import java.text.SimpleDateFormat
import java.util.Locale

class ChatAdapter(
    options: FirestoreRecyclerOptions<Message>,
    context: Context,
    private val scrollToLastMessage: () -> Unit
) : FirestoreRecyclerAdapter<Message, ChatAdapter.ChatViewHolder>(options) {
    val userId by lazy {
        context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
            .getString(LOCAL_USER_ID, null)
    }

    override fun onDataChanged() {
        super.onDataChanged()
        scrollToLastMessage()
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
            val messageIsFromUser = message.authorUid == userId
            setMessageGravity(messageIsFromUser)
            setMessageStyle(messageIsFromUser)

            itemView.chatMessageDate.text = message.dateCreated?.let {
                val pattern = SimpleDateFormat("HH:mm", Locale.FRANCE)
                pattern.format(message.dateCreated)
            } ?: "xx:xx"

            itemView.chatMessageContent.text = message.content
        }

        private fun setMessageStyle(isUserMessage: Boolean) {
            val backgroundColor = if (isUserMessage) {
                ResourcesCompat.getDrawable(
                    itemView.resources,
                    R.drawable.back_rounded_user_message,
                    null)
            } else {
                ResourcesCompat.getDrawable(
                    itemView.resources,
                    R.drawable.back_rounded_workmate_message,
                    null)
            }

            val txtColor = if (isUserMessage) {
                ResourcesCompat.getColor(itemView.resources, android.R.color.white, null)
            } else {
                ResourcesCompat.getColor(itemView.resources, android.R.color.black, null)
            }

            itemView.chatMessageContent.apply {
                background = backgroundColor
                textColor = txtColor
            }
        }

        private fun setMessageGravity(isUserMessage: Boolean) {
            val params = itemView.chatMessageContent.layoutParams as LinearLayout.LayoutParams
            params.gravity = if (isUserMessage) Gravity.END else Gravity.START
            itemView.chatMessageContent.layoutParams = params
        }
    }
}
