package com.rishi.dash3.Activties

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.rishi.dash3.Models.EachClass
import com.rishi.dash3.Models.EachCourse
import com.rishi.dash3.Adapters.InfoAdapter
import com.rishi.dash3.R
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_calendar.*

class CalendarActivity : AppCompatActivity(){
    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        realm = Realm.getDefaultInstance()


        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerViewClassesDay.layoutManager = layoutManager
        val adapter =
            InfoAdapter(
                this,
                mutableListOf(),
                false,
                realm,
                null
            )
        recyclerViewClassesDay.adapter = adapter
        val cal = Calendar.getInstance()
        var mDay = cal.get(Calendar.DAY_OF_WEEK)
        val weekDays = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        var mDate = "" + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(Calendar.YEAR)
        Toast.makeText(this, "$mDay $mDate", Toast.LENGTH_SHORT).show()
        var eachCrseCls: RealmResults<EachClass>


        calendarTT?.setOnDateChangeListener { _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR,year)
            cal.set(Calendar.MONTH,month)
            cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            mDate = String.format("%d/%d/%d",dayOfMonth,month+1,year)
            mDay = cal.get(Calendar.DAY_OF_WEEK)
            eachCrseCls = realm.where(EachClass::class.java).equalTo("day", weekDays[mDay-1]).`in`("date", arrayOf(mDate, "")).findAll()
            Toast.makeText(this, "$mDay $mDate", Toast.LENGTH_SHORT).show()
            val adapter = InfoAdapter(
                this,
                eachCrseCls.sortedWith(compareBy{ it.startTime }).toMutableList(),
                false,
                realm,
                null
            )
            recyclerViewClassesDay.adapter = adapter

        }


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






    }


    /*private fun getClses(allC: List<EachCls>, day: String, date:String): MutableList<EachCls> {

        var reqC: MutableList<EachCls> = mutableListOf()

        for (cls in allC) {
            if (cls.date == day || cls.date == date) {
                reqC.add(cls)
            }
        }
        return reqC
    }*/

    override fun onDestroy() {
        realm.close()
        super.onDestroy()
    }
}