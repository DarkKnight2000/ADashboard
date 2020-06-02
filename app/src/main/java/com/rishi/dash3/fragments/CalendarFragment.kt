package com.rishi.dash3.fragments


import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rishi.dash3.R
import com.rishi.dash3.adapters.InfoAdapter
import com.rishi.dash3.utils.getSeg
import com.rishi.dash3.models.EachClass
import com.rishi.dash3.models.Settings
import com.rishi.dash3.utils.weekDays
import io.realm.Realm


class CalendarFragment : Fragment() {

    lateinit var setts: Settings
    lateinit var realm: Realm
    private var mDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
        setts = realm.where(Settings::class.java).findFirst()!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val newView = inflater.inflate(R.layout.fragment_calendar, container, false)
        setts = realm.where(Settings::class.java).findFirst()!!

        val calView = newView.findViewById<CalendarView>(R.id.calendarTT)
        val dummy = newView.findViewById<TextView>(R.id.noClsView)

        val recyclerViewClassesDay = newView.findViewById<RecyclerView>(R.id.recyclerViewClassesDay)
        val layoutManager = LinearLayoutManager(context!!)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        newView.findViewById<RecyclerView>(R.id.recyclerViewClassesDay).layoutManager = layoutManager

        calView?.setOnDateChangeListener { _, year, month, dayOfMonth ->
            //Toast.makeText(context!!, "callist called", Toast.LENGTH_SHORT).show()
            setAdapter(recyclerViewClassesDay, dummy, year, month, dayOfMonth)
        }

        return newView
    }

    override fun onResume() {
        super.onResume()
        setts = realm.where(Settings::class.java).findFirst()!!
        val dummy = view!!.findViewById<TextView>(R.id.noClsView)
        val recyclerViewClassesDay = view!!.findViewById<RecyclerView>(R.id.recyclerViewClassesDay)
        val calView = view!!.findViewById<CalendarView>(R.id.calendarTT)
        val cal = Calendar.getInstance()

        if(mDate == ""){
            calView.date = cal.timeInMillis
            setAdapter(recyclerViewClassesDay, dummy, cal)
        }
        else{
            val parts = mDate.split("/")
            cal.set(Calendar.YEAR, parts[2].toInt())
            cal.set(Calendar.MONTH, parts[1].toInt()-1)
            cal.set(Calendar.DAY_OF_MONTH, parts[0].toInt())
            calView.date = cal.timeInMillis
            setAdapter(recyclerViewClassesDay, dummy, cal)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun setAdapter(rv:RecyclerView, dm:TextView, yr:Int, mth:Int, dy:Int){
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR,yr)
        cal.set(Calendar.MONTH,mth)
        cal.set(Calendar.DAY_OF_MONTH,dy)
        setAdapter(rv, dm, cal)
    }

    private fun setAdapter(rv:RecyclerView, dm:TextView, cal:Calendar){
        val month = cal.get(Calendar.MONTH)
        val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
        val year = cal.get(Calendar.YEAR)
        val mn = if(month < 10) "0"+(month+1) else (month+1).toString()
        val dy = if(dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
        mDate = String.format("%s/%s/%d", dy, mn, year)
        val mDay = cal.get(Calendar.DAY_OF_WEEK)
        //Toast.makeText(this.context!!, "$mDay $mDate", Toast.LENGTH_SHORT).show()
        val eachCrseCls = realm.where(EachClass::class.java).equalTo("day", weekDays[mDay-1]  + " " + getSeg(
            mDate,
            setts
        )
        ).`in`("date", arrayOf(mDate, "")).findAll()
        //Toast.makeText(this.context!!, "$mDay $mDate", Toast.LENGTH_SHORT).show()
        val adapterT = InfoAdapter(
            context!!,
            eachCrseCls.sortedWith(compareBy{ it.startTime }).toMutableList(),
            false,
            realm
        )
        rv.adapter = adapterT
        //Toast.makeText(context!!, "$mDay $mDate ${eachCrseCls.size}", Toast.LENGTH_SHORT).show()
        if(eachCrseCls.size > 0) dm.visibility = View.INVISIBLE
        else dm.visibility = View.VISIBLE
    }

}
