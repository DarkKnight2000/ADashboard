package com.rishi.dash3.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService


class SensorRestarter: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.i(
            "mytime",
            "Service Stops! Oooooooooooooppppssssss!!!!"
        )

        context.startService(Intent(context, NotifService::class.java))
    }
}