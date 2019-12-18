package com.rishi.dash3.Fragments


import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rishi.dash3.Adapters.InfoAdapter
import com.rishi.dash3.Models.EachClass
import com.rishi.dash3.Models.EachCourse
import com.rishi.dash3.Models.Settings
import com.rishi.dash3.R
import com.rishi.dash3.getSeg
import io.realm.Realm

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
        val testCalendar = view.findViewById<Button>(R.id.testCalendar)
        val layoutManager = LinearLayoutManager(this.context!!)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        view.findViewById<RecyclerView>(R.id.recyclerViewClassesDay).layoutManager = layoutManager
        val cal = Calendar.getInstance()
        var mDay = cal.get(Calendar.DAY_OF_WEEK)
        val weekDays = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
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
            mDate = String.format("%d/%d/%d",dayOfMonth,month+1,year)
            mDay = cal.get(Calendar.DAY_OF_WEEK)
            eachCrseCls = realm.where(EachClass::class.java).equalTo("day", weekDays[mDay-1]  + " " + getSeg(mDate, settings.semStart, settings.seg1End, settings.seg2End, settings.seg3End)).`in`("date", arrayOf(mDate, "")).findAll()
            //Toast.makeText(this.context!!, "$mDay $mDate", Toast.LENGTH_SHORT).show()
            val adapter = InfoAdapter(
                this.context!!,
                eachCrseCls.sortedWith(compareBy{ it.startTime }).toMutableList(),
                false,
                realm,
                null
            )
            recyclerViewClassesDay.adapter = adapter

        }
        calendarTT.setDate(calendarTT.getDate())


        testCalendar.setOnClickListener {
            realm.beginTransaction()
            realm.deleteAll()
            var newCrse = realm.createObject(EachCourse::class.java, "XXAAAA")
            newCrse.crsename = "N"
            newCrse.defSlot = "S"
            var tempcls = EachClass()
            tempcls.id = 1
            tempcls.endTime = 660
            tempcls.startTime = 720
            tempcls.room = "R"
            tempcls.date = "12/12/2019"
            newCrse.crseClsses.add(tempcls)
            realm.commitTransaction()
        }
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }


}