package com.rishi.dash3.fragments


import android.app.PendingIntent
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rishi.dash3.adapters.InfoAdapter
import com.rishi.dash3.Models.EachClass
import com.rishi.dash3.Models.Settings
import com.rishi.dash3.R
import com.rishi.dash3.getSeg
import com.rishi.dash3.services.NotifService
import com.rishi.dash3.services.SensorRestarter
import com.rishi.dash3.weekDays
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_calendar.*

class CalendarFragment : Fragment() {

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.activity_calendar, container, false)
        val settings = realm.where(Settings::class.java).findFirst()!!

        val calendarTT = view.findViewById<CalendarView>(R.id.calendarTT)
        val recyclerViewClassesDay = view.findViewById<RecyclerView>(R.id.recyclerViewClassesDay)
        val layoutManager = LinearLayoutManager(this.context!!)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        view.findViewById<RecyclerView>(R.id.recyclerViewClassesDay).layoutManager = layoutManager
        val cal = Calendar.getInstance()
        var mDay = cal.get(Calendar.DAY_OF_WEEK)
        var mDate = "" + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(
            Calendar.YEAR)
        //Toast.makeText(this.context!!, "$mDay $mDate", Toast.LENGTH_SHORT).show()
        var eachCrseCls = realm.where(EachClass::class.java).equalTo("day", weekDays[mDay-1]  + " " + getSeg(mDate, settings.semStart, settings.seg1End, settings.seg2End, settings.seg3End)).`in`("date", arrayOf(mDate, "")).findAll()
        //Toast.makeText(this.context!!, "$mDay $mDate", Toast.LENGTH_SHORT).show()
        val adapterT = InfoAdapter(
            this.context!!,
            eachCrseCls.sortedWith(compareBy{ it.startTime }).toMutableList(),
            false,
            realm,
            null
        )
        recyclerViewClassesDay.adapter = adapterT


        calendarTT?.setOnDateChangeListener { _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR,year)
            cal.set(Calendar.MONTH,month)
            cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            val mn = if(month < 10) "0"+(month+1) else (month+1).toString()
            val dy = if(dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
            val fDate = String.format("%s/%s/%d", dy, mn, year)
            mDay = cal.get(Calendar.DAY_OF_WEEK)
            eachCrseCls = realm.where(EachClass::class.java).equalTo("day", weekDays[mDay-1]  + " " + getSeg(fDate, settings.semStart, settings.seg1End, settings.seg2End, settings.seg3End)).`in`("date", arrayOf(fDate, "")).findAll()
            //Toast.makeText(this.context!!, "$mDay $mDate", Toast.LENGTH_SHORT).show()
            val adapter = InfoAdapter(
                this.context!!,
                eachCrseCls.sortedWith(compareBy{ it.startTime }).toMutableList(),
                false,
                realm,
                null
            )
            recyclerViewClassesDay.adapter = adapter
            dummy.height = recyclerViewClassesDay.height
        }
        calendarTT.setDate(calendarTT.getDate())
        return view
    }

    override fun onResume() {
        super.onResume()
        val settings = realm.where(Settings::class.java).findFirst()!!
        val cal = Calendar.getInstance()
        var mDay = cal.get(Calendar.DAY_OF_WEEK)
        var mDate = "" + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(
            Calendar.YEAR)
        var eachCrseCls = realm.where(EachClass::class.java).equalTo("day", weekDays[mDay-1]  + " " + getSeg(mDate, settings.semStart, settings.seg1End, settings.seg2End, settings.seg3End)).`in`("date", arrayOf(mDate, "")).findAll()
        //Toast.makeText(this.context!!, "$mDay $mDate", Toast.LENGTH_SHORT).show()
        val adapterT = InfoAdapter(
            this.context!!,
            eachCrseCls.sortedWith(compareBy{ it.startTime }).toMutableList(),
            false,
            realm,
            null
        )
        recyclerViewClassesDay.adapter = adapterT
    }


    /*override fun onStop() {
        super.onStop()
        val calendar: Calendar = Calendar.getInstance()
        Log.i("mytime", "ondestroy main")
        if(PendingIntent.getService(context?.applicationContext, 0, Intent(context?.applicationContext, NotifService::class.java), PendingIntent.FLAG_NO_CREATE) == null) {
            Log.i(
                "mytime",
                "broadcasting ${calendar.get(Calendar.HOUR_OF_DAY)},${calendar.get(Calendar.MINUTE)}"
            )
            val i = Intent(context?.applicationContext, SensorRestarter::class.java)
            context?.applicationContext?.sendBroadcast(i)
        }
    }*/

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }


}
