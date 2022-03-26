/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * This class will be used to determine whether to send breakfast, lunch, and dinner notifications
 * at certain times.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.di

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import ca.neunition.data.receiver.BreakfastAlarmReceiver
import ca.neunition.data.receiver.DinnerAlarmReceiver
import ca.neunition.data.receiver.LunchAlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@RequiresApi(Build.VERSION_CODES.O)
@Singleton
class NotificationsClass @Inject constructor(@ApplicationContext private val context: Context) {
    private lateinit var breakfastCalendar: Calendar
    private var breakfastAlarmMgr: AlarmManager? = null
    private lateinit var breakfastAlarmIntent: PendingIntent

    private lateinit var lunchCalendar: Calendar
    private var lunchAlarmMgr: AlarmManager? = null
    private lateinit var lunchAlarmIntent: PendingIntent

    private lateinit var dinnerCalendar: Calendar
    private var dinnerAlarmMgr: AlarmManager? = null
    private lateinit var dinnerAlarmIntent: PendingIntent

    fun switchMainPref(
        switch: Boolean,
        breakfastSwitchOn: Boolean,
        lunchSwitchOn: Boolean,
        dinnerSwitchOn: Boolean
    ) {
        if (switch) {
            breakfastSwitchPref(breakfastSwitchOn)
            lunchSwitchPref(lunchSwitchOn)
            dinnerSwitchPref(dinnerSwitchOn)
        } else {
            breakfastCancelAlarm()
            lunchCancelAlarm()
            dinnerCancelAlarm()
        }
    }

    /**
     * Create the NotificationChannel, but only on API 26+ because the NotificationChannel class is
     * new and not in the support library
     */
    fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val breakfastChannel = NotificationChannel(
            BREAKFAST_CHANNEL_ID,
            BREAKFAST_NOTIFICATION_NAME,
            importance
        ).apply { description = CHANNEL_DESCRIPTION }

        val lunchChannel = NotificationChannel(
            LUNCH_CHANNEL_ID,
            LUNCH_NOTIFICATION_NAME,
            importance
        ).apply { description = CHANNEL_DESCRIPTION }

        val dinnerChannel = NotificationChannel(
            DINNER_CHANNEL_ID,
            DINNER_NOTIFICATION_NAME,
            importance
        ).apply { description = CHANNEL_DESCRIPTION }

        // Register the channel with the system
        val notificationManager: NotificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(breakfastChannel)
        notificationManager.createNotificationChannel(lunchChannel)
        notificationManager.createNotificationChannel(dinnerChannel)
    }

    private fun breakfastAlarm() {
        breakfastCalendar = Calendar.getInstance()
        breakfastCalendar[Calendar.HOUR_OF_DAY] = 8
        breakfastCalendar[Calendar.MINUTE] = 0
        breakfastCalendar[Calendar.SECOND] = 0
        breakfastCalendar[Calendar.MILLISECOND] = 0

        if ((Calendar.getInstance().timeInMillis - breakfastCalendar.timeInMillis) > 0) {
            breakfastCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        breakfastAlarmMgr = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        breakfastAlarmIntent = Intent(
            context,
            BreakfastAlarmReceiver::class.java
        ).let { intent ->
            PendingIntent.getBroadcast(
                context,
                BREAKFAST_NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        breakfastAlarmMgr?.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            breakfastCalendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            breakfastAlarmIntent
        )
    }

    private fun lunchAlarm() {
        lunchCalendar = Calendar.getInstance()
        lunchCalendar[Calendar.HOUR_OF_DAY] = 12
        lunchCalendar[Calendar.MINUTE] = 0
        lunchCalendar[Calendar.SECOND] = 0
        lunchCalendar[Calendar.MILLISECOND] = 0

        if ((Calendar.getInstance().timeInMillis - lunchCalendar.timeInMillis) > 0) {
            lunchCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        lunchAlarmMgr = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        lunchAlarmIntent = Intent(
            context,
            LunchAlarmReceiver::class.java
        ).let { intent ->
            PendingIntent.getBroadcast(
                context,
                LUNCH_NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        lunchAlarmMgr?.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            lunchCalendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            lunchAlarmIntent
        )
    }

    private fun dinnerAlarm() {
        dinnerCalendar = Calendar.getInstance()
        dinnerCalendar[Calendar.HOUR_OF_DAY] = 18
        dinnerCalendar[Calendar.MINUTE] = 30
        dinnerCalendar[Calendar.SECOND] = 0
        dinnerCalendar[Calendar.MILLISECOND] = 0

        if ((Calendar.getInstance().timeInMillis - dinnerCalendar.timeInMillis) > 0) {
            dinnerCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        dinnerAlarmMgr = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        dinnerAlarmIntent = Intent(
            context,
            DinnerAlarmReceiver::class.java
        ).let { intent ->
            PendingIntent.getBroadcast(
                context,
                DINNER_NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        dinnerAlarmMgr?.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            dinnerCalendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            dinnerAlarmIntent
        )
    }

    fun breakfastCancelAlarm() {
        breakfastAlarmMgr?.cancel(breakfastAlarmIntent)
    }

    fun lunchCancelAlarm() {
        lunchAlarmMgr?.cancel(lunchAlarmIntent)
    }

    fun dinnerCancelAlarm() {
        dinnerAlarmMgr?.cancel(dinnerAlarmIntent)
    }

    fun breakfastSwitchPref(switch: Boolean) {
        if (switch) {
            breakfastAlarm()
        } else {
            breakfastCancelAlarm()
        }
    }

    fun lunchSwitchPref(switch: Boolean) {
        if (switch) {
            lunchAlarm()
        } else {
            lunchCancelAlarm()
        }
    }

    fun dinnerSwitchPref(switch: Boolean) {
        if (switch) {
            dinnerAlarm()
        } else {
            dinnerCancelAlarm()
        }
    }

    companion object {
        private const val CHANNEL_DESCRIPTION = "Channel for notifications alarm manager"

        private const val BREAKFAST_CHANNEL_ID = "BREAKFAST_NOTIFICATION"
        private const val BREAKFAST_NOTIFICATION_NAME = "Breakfast Reminder"
        private const val BREAKFAST_NOTIFICATION_ID = 111

        private const val LUNCH_CHANNEL_ID = "LUNCH_NOTIFICATION"
        private const val LUNCH_NOTIFICATION_NAME = "Lunch Reminder"
        private const val LUNCH_NOTIFICATION_ID = 222

        private const val DINNER_CHANNEL_ID = "DINNER_NOTIFICATION"
        private const val DINNER_NOTIFICATION_NAME = "Dinner Reminder"
        private const val DINNER_NOTIFICATION_ID = 333
    }
}
