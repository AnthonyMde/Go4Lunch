package com.my.anthonymamode.go4lunch.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.google.firebase.firestore.ktx.toObject
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.data.api.getCurrentUserData
import com.my.anthonymamode.go4lunch.data.api.getUsersByLunchId
import com.my.anthonymamode.go4lunch.domain.User
import com.my.anthonymamode.go4lunch.ui.home.HomeActivity
import com.my.anthonymamode.go4lunch.ui.home.INTENT_EXTRA_USER_ID

private const val CHANNEL_ID = "search_prefs_notification"

class NotificationBroadcastReceiver : BroadcastReceiver() {
    private lateinit var context: Context
    private lateinit var user: User
    private var coworkersName = mutableListOf<String?>()
    override fun onReceive(context: Context?, intent: Intent?) {
        val userId = intent?.getStringExtra(INTENT_EXTRA_USER_ID)
        if (context == null || userId == null) {
            return
        }
        this.context = context
        getCurrentUserData(userId).addOnSuccessListener { userData ->
            userData ?: return@addOnSuccessListener
            if (userData.hasLunch) {
                user = userData
                getCoworkersForThisPlace(user.lunch?.lunchOfTheDay)
            }
        }.addOnFailureListener {
            Log.e("NOTIFICATION", "OnFailure to get userData = ${it.message}")
        }
    }

    /**
     * Here we send the notification to the user with a
     * title, an icon, the restaurant he has chosen, its address and which colleagues
     * are also going there.
     */
    private fun sendNotification() {
        val placeName = user.lunch?.lunchName
        val placeAddress = user.lunch?.lunchAddress ?: "restaurant"
        coworkersName.remove(user.displayName)
        val contentText = if (coworkersName.isNotEmpty()) {
            val names = coworkersName.joinToString(separator = ", ", postfix = ".", limit = 3)
            context.getString(R.string.notification_content_with_coworkers, placeName, placeAddress, names)
        } else {
            context.getString(R.string.notification_content_without_coworkers, placeName, placeAddress)
        }

        val title = context.getString(R.string.notification_title)
        val resultIntent = Intent(context, HomeActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_login_logo_white)
            .setContentTitle(title)
            .setContentIntent(resultPendingIntent)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        val manager = NotificationManagerCompat.from(context)
        manager.notify(1, builder.build())
    }

    private fun getCoworkersForThisPlace(lunchOfTheDay: String?) {
        lunchOfTheDay ?: return
        // Notification is launched in this call back because we have to wait for the coworkers list
        getUsersByLunchId(lunchOfTheDay).get().addOnSuccessListener { coworkers ->
            if (coworkersName.isNotEmpty()) {
                coworkersName = mutableListOf()
            }
            coworkers.forEachIndexed { index, coworker ->
                coworkersName.add(index, coworker?.toObject<User>()?.displayName ?: "")
            }
            createNotificationChannel()
            sendNotification()
        }
            .addOnFailureListener {
                Log.e("NOTIFICATION", "OnFailure to get coworkers = ${it.message}")
            }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_channel_title)
            val description = context.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system. We can't change notification behaviors after.
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
}
