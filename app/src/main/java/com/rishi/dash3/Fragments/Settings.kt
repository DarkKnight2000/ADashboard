package com.rishi.dash3.Fragments


import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rishi.dash3.Adapters.ClassesAdapter
import com.rishi.dash3.Models.EachClass
import com.rishi.dash3.Models.EachCourse
import com.rishi.dash3.R
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_courseinfo.*


class Settings : Fragment() {

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val startSem = view.findViewById<TextView>(R.id.semStart)
        startSem.setOnClickListener(dateSetter(this.context!!, startSem))
        val ids = arrayOf(R.id.seg1, R.id.seg2, R.id.seg3)
        var s:TextView
        for(id in ids){
            s = view.findViewById(id)
            s.setOnClickListener(dateSetter(this.context!!, s))
        }
        return view
    }

    private fun dateSetter(context:Context, view:TextView) : View.OnClickListener{
        return View.OnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, mnth, day ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, mnth)
                cal.set(Calendar.DAY_OF_MONTH, day)
                val mDate = String.format("%d/%d/%d", day, mnth + 1, year)
                view.text = mDate
            }
            DatePickerDialog(
                context, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(
                    Calendar.DAY_OF_MONTH
                )
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
