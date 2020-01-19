package com.rishi.dash3.services


import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rishi.dash3.Models.EachClass
import com.rishi.dash3.Models.EachCourse
import com.rishi.dash3.Models.Settings
import com.rishi.dash3.R
import com.rishi.dash3.getSeg
import com.rishi.dash3.intToTime
import com.rishi.dash3.weekDays
import io.realm.Realm


class NotifService: Service() {

    private var nextTime = -1
    private var nextCls:EachClass? = null
    private var name:String? = ""
    private val realm = Realm.getDefaultInstance()
    private var calendar: Calendar = Calendar.getInstance()
    private val settings = realm.where(Settings::class.java).findFirst()!!
    private var before = 1

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        var mDay = calendar.get(Calendar.DAY_OF_WEEK)
        var mDate = "" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.YEAR)
        var s = realm.where(EachClass::class.java).equalTo("day", weekDays[mDay-1]  + " " + getSeg(mDate, settings.semStart, settings.seg1End, settings.seg2End, settings.seg3End)).`in`("date", arrayOf(mDate, "")).greaterThan("startTime", (calendar.get(Calendar.HOUR_OF_DAY)*60+calendar.get(Calendar.MINUTE))).min("startTime")?.toInt()
        if(s == null){
            calendar.add(Calendar.DATE, 1)
            mDay = calendar.get(Calendar.DAY_OF_WEEK)
            mDate = "" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.YEAR)
            s = realm.where(EachClass::class.java).equalTo("day", weekDays[mDay-1]  + " " + getSeg(mDate, settings.semStart, settings.seg1End, settings.seg2End, settings.seg3End)).`in`("date", arrayOf(mDate, "")).min("startTime")?.toInt()
            if(s!=null){
                nextTime = s-before
                nextCls = realm.where(EachClass::class.java).equalTo("day", weekDays[mDay-1]  + " " + getSeg(mDate, settings.semStart, settings.seg1End, settings.seg2End, settings.seg3End)).`in`("date", arrayOf(mDate, "")).equalTo("startTime", s).findFirst()
                name = realm.where(EachCourse::class.java).equalTo("crsecode", nextCls?.code).findFirst()?.crsename
            }
            else nextTime = -1
        }
        else {
            nextTime = s-before
            nextCls = realm.where(EachClass::class.java).equalTo("day", weekDays[mDay-1]  + " " + getSeg(mDate, settings.semStart, settings.seg1End, settings.seg2End, settings.seg3End)).`in`("date", arrayOf(mDate, "")).equalTo("startTime", s).findFirst()
            name = realm.where(EachCourse::class.java).equalTo("crsecode", nextCls?.code).findFirst()?.crsename
        }
        Log.i("mytime","oncreate $nextTime, $nextCls, ${calendar.time}")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId);
        calendar = Calendar.getInstance()
        if(nextTime != calendar.get(Calendar.HOUR_OF_DAY)*60+calendar.get(Calendar.MINUTE)) {
            calendar = Calendar.getInstance()
            //calendar.timeInMillis = (System.currentTimeMillis())
            calendar.set(Calendar.HOUR_OF_DAY, nextTime / 60)
            calendar.set(Calendar.MINUTE, nextTime.rem(60))
            calendar.set(Calendar.SECOND, 0)
            val alarm = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            alarm[AlarmManager.RTC_WAKEUP, calendar.timeInMillis] = PendingIntent.getBroadcast(this, 0, Intent(this, SensorRestarter::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
            Log.i("mytime", "suspended succesfully!2")
            stopSelf()
            return START_NOT_STICKY
        }
        val notifManager = NotificationManagerCompat.from(this)
        val notif = NotificationCompat.Builder(this, "1")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Class alert!")
            .setContentText("You have $name class at ${intToTime(nextCls!!.startTime)}")
            .build()
        notifManager.notify(1, notif)
        var mDay = calendar.get(Calendar.DAY_OF_WEEK)
        var mDate = "" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.YEAR)
        var s = realm.where(EachClass::class.java).equalTo("day", weekDays[mDay-1]  + " " + getSeg(mDate, settings.semStart, settings.seg1End, settings.seg2End, settings.seg3End)).`in`("date", arrayOf(mDate, "")).greaterThan("startTime", (calendar.get(Calendar.HOUR_OF_DAY)*60+calendar.get(Calendar.MINUTE))+before).min("startTime")?.toInt()
        if(s == null){
            calendar.add(Calendar.DATE, 1)
            mDay = calendar.get(Calendar.DAY_OF_WEEK)
            mDate = "" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.YEAR)
            s = realm.where(EachClass::class.java).equalTo("day", weekDays[mDay-1]  + " " + getSeg(mDate, settings.semStart, settings.seg1End, settings.seg2End, settings.seg3End)).`in`("date", arrayOf(mDate, "")).min("startTime")?.toInt()
            if(s!=null){
                nextTime = s-before
                nextCls = realm.where(EachClass::class.java).equalTo("day", weekDays[mDay-1]  + " " + getSeg(mDate, settings.semStart, settings.seg1End, settings.seg2End, settings.seg3End)).`in`("date", arrayOf(mDate, "")).equalTo("startTime", s).findFirst()
                name = realm.where(EachCourse::class.java).equalTo("crsecode", nextCls?.code).findFirst()?.crsename
            }
            else nextTime = -1
        }
        else {
            nextTime = s-before
            nextCls = realm.where(EachClass::class.java).equalTo("day", weekDays[mDay-1]  + " " + getSeg(mDate, settings.semStart, settings.seg1End, settings.seg2End, settings.seg3End)).`in`("date", arrayOf(mDate, "")).equalTo("startTime", s).findFirst()
            name = realm.where(EachCourse::class.java).equalTo("crsecode", nextCls?.code).findFirst()?.crsename
        }
        Log.i("mytime","start $nextTime, $nextCls, ${calendar.time}")
        calendar = Calendar.getInstance()
        //calendar.timeInMillis = (System.currentTimeMillis())
        calendar.set(Calendar.HOUR_OF_DAY, nextTime / 60)
        calendar.set(Calendar.MINUTE, nextTime.rem(60))
        calendar.set(Calendar.SECOND, 0)
        val alarm = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarm[AlarmManager.RTC_WAKEUP, calendar.timeInMillis] = PendingIntent.getBroadcast(this, 0, Intent(this, SensorRestarter::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
        Log.i("mytime", "suspended succesfully!1")
        stopSelf()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if(nextTime != -1) {
            Log.i(
                "mytime",
                "destroy ${calendar.get(Calendar.HOUR_OF_DAY)},${calendar.get(Calendar.MINUTE)}"
            )
        }
    }
}