package com.rishi.dash3.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.rishi.dash3.utils.enqueueWork

class NotifBroadcastRcvr : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Log.i("Broadcast", "Fired1 with id ${intent.getLongExtra("ClassId", -1)}")
        if(intent.action == Intent.ACTION_BOOT_COMPLETED){
            intent.putExtra("ClassId", (-1).toLong())
        }

        //val serviceIntent = Intent(context, NotifService::class.java)
        //context.startService(serviceIntent)

        val mIntent = Intent(context, NotifService::class.java)
        mIntent.putExtra("ClassId", intent.getLongExtra("ClassId", (-1).toLong()))
        enqueueWork(context, mIntent)
        Log.i("Broadcast", "Fired ended")
    }
}