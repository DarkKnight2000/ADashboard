package com.rishi.dash3.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.rishi.dash3.R
import com.rishi.dash3.activties.MainActivity
import com.rishi.dash3.getSeg
import com.rishi.dash3.intToTime
import com.rishi.dash3.models.EachClass
import com.rishi.dash3.models.EachCourse
import com.rishi.dash3.models.Settings
import com.rishi.dash3.weekDays
import io.realm.Realm
import java.util.*


class NotifService : JobIntentService() {


    private val TAG = "MyJobIntentService"
    private var cal = Calendar.getInstance()


    override fun onHandleWork(intent: Intent) {

        Log.i("Broadcast", "Started jobintent with id ${intent.getLongExtra("ClassId", -1)}")


        val realm = Realm.getDefaultInstance()

        // Send notification using details from intent
        if(intent.getLongExtra("ClassId", (-1).toLong()) != (-1).toLong()) {
            val cls = realm.where(EachClass::class.java).equalTo("id", intent.getLongExtra("ClassId", (-1).toLong())).findFirst()
            if(cls != null) {
                var crseName = realm.where(EachCourse::class.java).equalTo("crsecode", cls.code)
                    .findFirst()?.crsename
                if (crseName == null) crseName = ""
                val resultIntent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val pendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                    // Add the intent, which inflates the back stack
                    addNextIntentWithParentStack(resultIntent)
                    // Get the PendingIntent containing the entire back stack
                    getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                }

                val builder = NotificationCompat.Builder(this, ClsNotifChannelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Class Alert!")
                    .setContentText("You have a $crseName class at ${intToTime(cls.startTime)} in room ${cls.room}")
                    .setGroup(ClsNotifGrpId)
                    .setGroupSummary(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                with(NotificationManagerCompat.from(this)) {
                    // notificationId is a unique int for each notification that you must define
                    notify(cls.id.toInt(), builder.build())
                }
            }
        }
        else{
            Log.i("Broadcast", "got null id")
        }

        // Getting next class
        val nextClsId = getNextClass()

        if(nextClsId != null){
            val nextCls = realm.where(EachClass::class.java).equalTo("id", nextClsId).findFirst()!!
            var value = nextCls.startTime
            value -= alertBefore
            cal.set(Calendar.HOUR_OF_DAY, value/60)
            cal.set(Calendar.MINUTE, value.rem(60))
            cal.set(Calendar.SECOND, 0)
            Log.i("Broadcast", "${cal.get(Calendar.HOUR_OF_DAY)}:${cal.get(Calendar.MINUTE)}")


            // Set alarm manager for next notification
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(this, NotifBroadcastRcvr::class.java)
            alarmIntent.putExtra("ClassId", nextCls.id)
            Log.i("Broadcast", "Put extra ${alarmIntent.getLongExtra("ClassId", (-1).toLong())}")
            val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
            Log.i("Broadcast", "Alarm set after ${cal.timeInMillis - Calendar.getInstance().timeInMillis} at ${cal.time}")
        }
        else {
            // Last class in timetable
            //alarmManager.set(AlarmManager.RTC_WAKEUP, cal.timeInMillis + 3000, pendingIntent)
            Log.i("Broadcast", "LAst class")
        }
        realm.close()
    }

    fun getNextClass():Long?{
        cal = Calendar.getInstance()
        val realm = Realm.getDefaultInstance()
        val settings = realm.where(Settings::class.java).findFirst()!!
        var mDay = cal.get(Calendar.DAY_OF_WEEK)
        var mDate = "" + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(
            android.icu.util.Calendar.YEAR)
        var todaysClsQuery = realm.where(EachClass::class.java).equalTo("day", weekDays[mDay-1]  + " " + getSeg(mDate, settings.semStart, settings.seg1End, settings.seg2End, settings.seg3End)).`in`("date", arrayOf(mDate, ""))
            .greaterThan("startTime", cal.get(Calendar.HOUR_OF_DAY)*60 + cal.get(Calendar.MINUTE) + alertBefore)

        var nextClsTime = todaysClsQuery.min("startTime")?.toInt()
        while(nextClsTime == null && getSeg(mDate, settings) != ""){
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.add(Calendar.DAY_OF_MONTH, 1)
            mDay = cal.get(Calendar.DAY_OF_WEEK)
            mDate = "" + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(Calendar.YEAR)
            Log.i("Broadcast", "Checking for data $mDate")
            todaysClsQuery = realm.where(EachClass::class.java).equalTo("day", weekDays[mDay-1]  + " " + getSeg(mDate, settings.semStart, settings.seg1End, settings.seg2End, settings.seg3End)).`in`("date", arrayOf(mDate, ""))
                .greaterThan("startTime", cal.get(Calendar.HOUR_OF_DAY)*60 + cal.get(Calendar.MINUTE) + alertBefore)

            nextClsTime = todaysClsQuery.min("startTime")?.toInt()
        }
        if(nextClsTime != null){
            val nextCls = realm.where(EachClass::class.java).equalTo("day", weekDays[mDay-1]  + " " + getSeg(mDate, settings.semStart, settings.seg1End, settings.seg2End, settings.seg3End)).`in`("date", arrayOf(mDate, ""))
                .greaterThan("startTime", cal.get(Calendar.HOUR_OF_DAY)*60 + cal.get(Calendar.MINUTE) + alertBefore).equalTo("startTime", nextClsTime).findFirst()
            realm.close()
            return nextCls!!.id
        }
        realm.close()
        return null
    }

}
