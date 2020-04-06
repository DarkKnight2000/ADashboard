package com.rishi.dash3.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rishi.dash3.R
import java.util.*


class NotifService : JobIntentService() {


    private val TAG = "MyJobIntentService"


    override fun onHandleWork(intent: Intent) {

        Log.i("Broadcast", "Started jobintent")

        // Send notification
        var builder = NotificationCompat.Builder(this, ClsNotifChannelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Test")
            .setContentText("TextContent")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder.build())
        }

        // Set alarm manager for next notification
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, NotifBroadcastRcvr::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0)

        val cal: Calendar = Calendar.getInstance()
        cal.setTimeInMillis(System.currentTimeMillis())

        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.timeInMillis + 3000, pendingIntent)
    }

}