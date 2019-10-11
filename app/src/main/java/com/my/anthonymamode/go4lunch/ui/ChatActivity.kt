package com.my.anthonymamode.go4lunch.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.utils.BaseActivity
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : BaseActivity() {
    private var workmateId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        workmateId = intent.getStringExtra(WORKMATE_ID)

        testText.text = "Bienvenue sur le chat de Monsieur ID : $workmateId"
    }

    companion object {
        const val WORKMATE_ID = "WORKMATE_ID"
        fun navigateToChatActivity(workmateId: String, context: Context) {
            val intent = Intent(context, ChatActivity::class.java).putExtra(WORKMATE_ID, workmateId)
            context.startActivity(intent)
        }
    }
}
