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
class BreakfastAlarmReceiver : BroadcastReceiver() {
    @Inject lateinit var notificationsClass: NotificationsClass

    override fun onReceive(context: Context, intent: Intent) {
        val i = Intent(context, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, Constants.BREAKFAST_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setContentTitle(context.getString(R.string.breakfast_notification))
            .setContentText(BREAKFAST_CONTEXT_TEXT)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle().bigText(BREAKFAST_CONTEXT_TEXT))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(Constants.BREAKFAST_NOTIFICATION_ID, builder.build())
        }

        notificationsClass.breakfastAlarm()
    }

    companion object {
        private val BREAKFAST_CONTEXT_TEXT: String by lazy {
            "There's nothing like starting the day with an environmentally friendly breakfast. Let's record the GHG emissions for what you ate!"
        }
    }
}
