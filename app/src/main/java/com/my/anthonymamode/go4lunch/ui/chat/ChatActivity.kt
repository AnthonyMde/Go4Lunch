package com.my.anthonymamode.go4lunch.ui.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.utils.base.BaseActivity
import kotlinx.android.synthetic.main.activity_chat.*
import org.jetbrains.anko.toast

class ChatActivity : BaseActivity() {
    private var workmateId: String? = null
    private var workmateName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        workmateId = intent.getStringExtra(WORKMATE_ID)
        workmateName = intent.getStringExtra(WORKMATE_NAME)

        setupToolbar()
        setupInputBar()
    }

    private fun setupInputBar() {
        chatInputSend.setOnClickListener { toast("not implemented yet") }
    }

    private fun setupToolbar() {
        setSupportActionBar(chatToolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = workmateName
        }
    }

    companion object {
        const val WORKMATE_ID = "WORKMATE_ID"
        const val WORKMATE_NAME = "WORKMATE_NAME"
        fun navigateToChatActivity(workmateId: String, workmateName: String?, context: Context) {
            val intent = Intent(context, ChatActivity::class.java).apply {
                putExtra(WORKMATE_ID, workmateId)
                putExtra(WORKMATE_NAME, workmateName)
            }
            context.startActivity(intent)
        }
    }
}
