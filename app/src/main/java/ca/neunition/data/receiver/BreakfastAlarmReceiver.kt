/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * BroadcastReceiver for sending a notification during breakfast time (morning).
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.data.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import ca.neunition.R
import ca.neunition.ui.main.view.SplashActivity

class BreakfastAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val i = Intent(context, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            i,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setContentTitle(context.getString(R.string.breakfast_notification))
            .setContentText(CONTEXT_TEXT)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle().bigText(CONTEXT_TEXT))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    companion object {
        private const val CHANNEL_ID = "BREAKFAST_NOTIFICATION"
        private const val NOTIFICATION_ID = 111
        private const val CONTEXT_TEXT =
            "There's nothing like starting the day with an environmentally friendly breakfast. Let's record the GHG emissions for what you ate!"
    }
}
