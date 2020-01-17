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
            SensorRestarter::class.java.simpleName,
            "Service Stops! Oooooooooooooppppssssss!!!!"
        )

        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarm[AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+5000] =
            PendingIntent.getService(context, 0, Intent(context, NotifService::class.java), 0)
        //context.startService(Intent(context, NotifService::class.java))
    }
}