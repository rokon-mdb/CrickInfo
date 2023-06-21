package com.kamrulhasan.crickinfo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.kamrulhasan.crickinfo.utils.MyNotification

private const val TAG = "MyBroadcastReceiver"

class MyBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val message = intent.getStringExtra("message") ?: "Live Match Notification"
        MyNotification.makeStatusNotification(message)

        Log.d(TAG, "onReceive: alarm is ringing")
    }
}