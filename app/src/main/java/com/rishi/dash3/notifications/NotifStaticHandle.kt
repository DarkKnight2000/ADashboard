package com.rishi.dash3.notifications

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.JobIntentService


/**
 * Unique job ID for this service.
 */
private val JOB_ID = 2
fun enqueueWork(context: Context, intent: Intent) {
    JobIntentService.enqueueWork(context, NotifService::class.java, JOB_ID, intent)
}

var sendNotif = true
val ClsNotifChannelId = "1"
val ClsNotifGrpId = "Class Alerts!"

// In minutes
val alertBefore = 10

@SuppressLint("ObsoleteSdkInt")
fun restartNotifService(context: Context){
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val alarmIntent = Intent(context, NotifBroadcastRcvr::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
    } else {
        alarmManager.set(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
    }
}

@SuppressLint("ObsoleteSdkInt")
fun stopNotifService(context: Context){
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val alarmIntent = Intent(context, NotifBroadcastRcvr::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)

    alarmManager.cancel(pendingIntent);
    Toast.makeText(context.applicationContext, "Notifications Cancelled", Toast.LENGTH_LONG).show();
}