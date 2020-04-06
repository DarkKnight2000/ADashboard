package com.rishi.dash3.notifications

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService


/**
 * Unique job ID for this service.
 */
private val JOB_ID = 2
fun enqueueWork(context: Context, intent: Intent) {
    JobIntentService.enqueueWork(context, NotifService::class.java, JOB_ID, intent)
}

val ClsNotifChannelId = "1"