package com.rishi.dash3.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotifBroadcastRcvr : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        /*Toast.makeText(
            context.applicationContext,
            "Alarm Manager just ran1",
            Toast.LENGTH_SHORT
        ).show()*/
        Log.i("Broadcast", "Fired1")

        //val serviceIntent = Intent(context, NotifService::class.java)
        //context.startService(serviceIntent)

        val mIntent = Intent(context, NotifService::class.java)
        mIntent.putExtra("maxCountValue", 1000)
        enqueueWork(context, intent)

    }
}