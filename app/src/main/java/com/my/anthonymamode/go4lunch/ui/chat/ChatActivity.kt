package com.my.anthonymamode.go4lunch.ui.chat

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.data.api.getChatMessages
import com.my.anthonymamode.go4lunch.data.api.postMessage
import com.my.anthonymamode.go4lunch.ui.LOCAL_USER_ID
import com.my.anthonymamode.go4lunch.ui.SHARED_PREFS
import com.my.anthonymamode.go4lunch.utils.base.BaseActivity
import com.my.anthonymamode.go4lunch.utils.generateOptionForAdapter
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : BaseActivity() {
    private var workmateId: String? = null
    private var workmateName: String? = null
    private lateinit var mAdapter: ChatAdapter
    private val userId by lazy {
        getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
            .getString(LOCAL_USER_ID, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        workmateId = intent.getStringExtra(WORKMATE_ID)
        workmateName = intent.getStringExtra(WORKMATE_NAME)

        setupToolbar()
        setupInputBar()
        configureRecyclerView()
    }

    private fun configureRecyclerView() {
        val uid = userId ?: return
        val wuid = workmateId ?: return
        mAdapter = ChatAdapter(
            options = generateOptionForAdapter(getChatMessages(uid, wuid), this),
            context = this
        )
        chatMessageList.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupInputBar() {
        chatInputSend.setOnClickListener {
            val uid = userId ?: return@setOnClickListener
            val wuid = workmateId ?: return@setOnClickListener
            val content = chatInput.text.toString()
            chatInput.text?.clear()
            postMessage(uid, wuid, content)
        }
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
        fun navigateToChatActivity(
            workmateId: String,
            workmateName: String?,
            context: Context,
            activity: BaseActivity
        ) {
            val intent = Intent(context, ChatActivity::class.java).apply {
                putExtra(WORKMATE_ID, workmateId)
                putExtra(WORKMATE_NAME, workmateName)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                context.startActivity(
                    intent,
                    ActivityOptions.makeSceneTransitionAnimation(activity).toBundle()
                )
            } else {
                context.startActivity(intent)
            }
        }
    }
}
