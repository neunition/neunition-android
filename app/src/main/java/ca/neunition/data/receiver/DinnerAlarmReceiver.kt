/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * BroadcastReceiver for sending a notification during dinner time (evening).
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
import ca.neunition.ui.main.view.SplashActivity
import ca.neunition.util.Constants

@RequiresApi(Build.VERSION_CODES.O)
class DinnerAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val i = Intent(context, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            i,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, Constants.DINNER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setContentTitle(context.getString(R.string.dinner_notification))
            .setContentText(Constants.DINNER_CONTEXT_TEXT)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle().bigText(Constants.DINNER_CONTEXT_TEXT))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(Constants.DINNER_NOTIFICATION_ID, builder.build())
        }
    }
}
