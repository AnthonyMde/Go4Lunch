package com.my.anthonymamode.go4lunch.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.domain.Message

class ChatAdapter(
    options: FirestoreRecyclerOptions<Message>
) : FirestoreRecyclerAdapter<Message, ChatAdapter.ChatViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int, message: Message) {
        super.onBindViewHolder(holder, position)
    }

    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
