package com.my.anthonymamode.go4lunch.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.my.anthonymamode.go4lunch.R

private const val CHANNEL_ID = "search_prefs_notification"

class NotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("NOTIFICATIONALARM", "ON RECEIVE TRIGGERED BEFORE")
        if (context == null) {
            return
        }
        Log.d("NOTIFICATIONALARM", "ON RECEIVE TRIGGERED AFTER")
        Toast.makeText(context, "NOTIFICATIONS !!!", Toast.LENGTH_LONG).show()
        createNotificationChannel(context)
        sendNotification(context)
    }

    /**
     * Here we send the notification to the user with a
     * title, an icon and the number of article we found.
     */
    private fun sendNotification(context: Context) {
        val contentText =
            "Ton restaurant et les gens qui y mangent"
        val title = "Lunch time"
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_login_logo_white)
            .setContentTitle(title)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val manager = NotificationManagerCompat.from(context)
        manager.notify(1, builder.build())
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "time to lunch"
            val description = "Voici votre restaurant"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system. We can't change notification behaviors after.
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
}
