package com.kamrulhasan.crickinfo.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kamrulhasan.crickinfo.R
import com.kamrulhasan.crickinfo.receiver.MyBroadcastReceiver
import com.kamrulhasan.crickinfo.ui.MainActivity
import java.util.*

private const val TAG = "Notification"

class MyNotification {
    companion object {
        ///////////////////////////////
        /////  make notification  /////
        ///////////////////////////////
        @SuppressLint("MissingPermission", "UnspecifiedImmutableFlag")
        fun makeStatusNotification(message: String) {

            val notificationIntent = Intent(MyApplication.appContext, MainActivity::class.java)
            notificationIntent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            val contentIntent = PendingIntent.getActivity(
                MyApplication.appContext,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

            // Make a channel if necessary
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create the NotificationChannel, but only on API 26+ because
                // the NotificationChannel class is new and not in the support library
                val name = VERBOSE_NOTIFICATION_CHANNEL_NAME
                val description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, name, importance)
                channel.description = description

                // Add the channel
                val notificationManager =
                    MyApplication.appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

                notificationManager?.createNotificationChannel(channel)
            }

            Log.d(TAG, "makeStatusNotification: $message")
            // Create the notification
            val builder = NotificationCompat.Builder(MyApplication.appContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_match)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(message)
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(LongArray(0))
                .setAutoCancel(true)

            // Show the notification
            try {
                NotificationManagerCompat.from(MyApplication.appContext)
                    .notify(NOTIFICATION_ID, builder.build())
            } catch (e: Exception) {
                Log.d(TAG, "makeStatusNotification: $e")
            }
        }

        ////////////////////////////////////////
        /////  set Alarm for notification  /////
        ////////////////////////////////////////

        @SuppressLint("UnspecifiedImmutableFlag")
        fun scheduleNotification(delay: Long, message: String) {

            val intent = Intent(MyApplication.appContext, MyBroadcastReceiver::class.java)
            intent.putExtra("message", message)

            val pendingIntent = PendingIntent.getBroadcast(
                MyApplication.appContext,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager =
                MyApplication.appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            /*val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                add(Calendar.SECOND, 3)
            }*/
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                delay,
                pendingIntent
            )
        }

    }
}