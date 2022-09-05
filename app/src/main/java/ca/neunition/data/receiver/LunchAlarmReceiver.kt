/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * BroadcastReceiver for sending a notification during lunch time (afternoon).
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.data.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import ca.neunition.R
import ca.neunition.di.NotificationsClass
import ca.neunition.ui.main.view.SplashActivity
import ca.neunition.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class LunchAlarmReceiver : BroadcastReceiver() {
    @Inject lateinit var notificationsClass: NotificationsClass

    override fun onReceive(context: Context, intent: Intent) {
        val i = Intent(context, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, Constants.LUNCH_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setContentTitle(context.getString(R.string.lunch_notification))
            .setContentText(LUNCH_CONTEXT_TEXT)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle().bigText(LUNCH_CONTEXT_TEXT))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(Constants.LUNCH_NOTIFICATION_ID, builder.build())
        }

        notificationsClass.lunchAlarm()
    }

    companion object {
        private val LUNCH_CONTEXT_TEXT: String by lazy {
            "You've been working very hard. Take a break and recharge with a meal that helps fight climate change! Remember to record your GHG emissions for lunch."
        }
    }
}
