/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * This class will be used to determine whether to send breakfast, lunch, and dinner notifications
 * to the user at certain times.
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
import ca.neunition.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@RequiresApi(Build.VERSION_CODES.O)
@Singleton
class NotificationsClass @Inject constructor(@ApplicationContext private val context: Context) {
    private val notificationManager: NotificationManager = context
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private lateinit var breakfastCalendar: Calendar
    private var breakfastAlarmMgr: AlarmManager? = null
    private lateinit var breakfastAlarmIntent: PendingIntent

    private lateinit var lunchCalendar: Calendar
    private var lunchAlarmMgr: AlarmManager? = null
    private lateinit var lunchAlarmIntent: PendingIntent

    private lateinit var dinnerCalendar: Calendar
    private var dinnerAlarmMgr: AlarmManager? = null
    private lateinit var dinnerAlarmIntent: PendingIntent

    /**
     * Activate notifications.
     *
     * @param switch The main toggle switch in [SettingsFragment] for turning on/off notifications
     * @param breakfastSwitchOn Turn on or off Breakfast Reminder notification
     * @param lunchSwitchOn Turn on or off Lunch Reminder notification
     * @param dinnerSwitchOn Turn on or off Dinner Reminder notification
     */
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
     * new and not in the support library.
     */
    fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val breakfastChannel = NotificationChannel(
            Constants.BREAKFAST_CHANNEL_ID,
            BREAKFAST_NOTIFICATION_NAME,
            importance
        ).apply { description = BREAKFAST_CHANNEL_DESCRIPTION }

        val lunchChannel = NotificationChannel(
            Constants.LUNCH_CHANNEL_ID,
            LUNCH_NOTIFICATION_NAME,
            importance
        ).apply { description = LUNCH_CHANNEL_DESCRIPTION }

        val dinnerChannel = NotificationChannel(
            Constants.DINNER_CHANNEL_ID,
            DINNER_NOTIFICATION_NAME,
            importance
        ).apply { description = DINNER_CHANNEL_DESCRIPTION }

        notificationManager.createNotificationChannel(breakfastChannel)
        notificationManager.createNotificationChannel(lunchChannel)
        notificationManager.createNotificationChannel(dinnerChannel)
    }

    /**
     * Turn on and send Breakfast Reminder notification.
     */
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
                Constants.BREAKFAST_NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }

        breakfastAlarmMgr?.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            breakfastCalendar.timeInMillis,
            breakfastAlarmIntent
        )
    }

    /**
     * Turn on and send Lunch Reminder notification.
     */
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
                Constants.LUNCH_NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }

        lunchAlarmMgr?.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            lunchCalendar.timeInMillis,
            lunchAlarmIntent
        )
    }

    /**
     * Turn on and send Dinner Reminder notification.
     */
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
                Constants.DINNER_NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }

        dinnerAlarmMgr?.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            dinnerCalendar.timeInMillis,
            dinnerAlarmIntent
        )
    }

    /**
     * Turn off Breakfast Reminder notification.
     */
    fun breakfastCancelAlarm() {
        breakfastAlarmMgr?.cancel(breakfastAlarmIntent)
        notificationManager.cancel(Constants.BREAKFAST_NOTIFICATION_ID)
    }

    /**
     * Turn off Lunch Reminder notification.
     */
    fun lunchCancelAlarm() {
        lunchAlarmMgr?.cancel(lunchAlarmIntent)
        notificationManager.cancel(Constants.LUNCH_NOTIFICATION_ID)
    }

    /**
     * Turn off Dinner Reminder notification.
     */
    fun dinnerCancelAlarm() {
        dinnerAlarmMgr?.cancel(dinnerAlarmIntent)
        notificationManager.cancel(Constants.DINNER_NOTIFICATION_ID)
    }

    /**
     * Determine whether to turn on or off the Breakfast Reminder notification.
     */
    fun breakfastSwitchPref(switch: Boolean) {
        if (switch) {
            breakfastAlarm()
        } else {
            breakfastCancelAlarm()
        }
    }

    /**
     * Determine whether to turn on or off the Lunch Reminder notification.
     */
    fun lunchSwitchPref(switch: Boolean) {
        if (switch) {
            lunchAlarm()
        } else {
            lunchCancelAlarm()
        }
    }

    /**
     * Determine whether to turn on or off the Dinner Reminder notification.
     */
    fun dinnerSwitchPref(switch: Boolean) {
        if (switch) {
            dinnerAlarm()
        } else {
            dinnerCancelAlarm()
        }
    }

    companion object {
        private val BREAKFAST_NOTIFICATION_NAME: String by lazy { "Breakfast Reminder" }
        private val LUNCH_NOTIFICATION_NAME: String by lazy { "Lunch Reminder" }
        private val DINNER_NOTIFICATION_NAME: String by lazy { "Dinner Reminder" }

        private val BREAKFAST_CHANNEL_DESCRIPTION: String by lazy { "Reminder to record your GHG emissions for breakfast." }
        private val LUNCH_CHANNEL_DESCRIPTION: String by lazy { "Reminder to record your GHG emissions for lunch." }
        private val DINNER_CHANNEL_DESCRIPTION: String by lazy { "Reminder to record your GHG emissions for dinner." }
    }
}
