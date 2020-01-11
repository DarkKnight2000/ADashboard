package com.rishi.dash3.fragments


import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.rishi.dash3.Models.EachClass
import com.rishi.dash3.Models.EachCourse
import com.rishi.dash3.Models.Settings
import com.rishi.dash3.R
import com.rishi.dash3.getSeg
import com.rishi.dash3.isGreaterDate
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_settings.*
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException


class Settings : Fragment() {

    lateinit var realm: Realm
    val fileName = "Data.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
        if(realm.where(Settings::class.java).findFirst() == null) {
            realm.beginTransaction()
            val set = realm.createObject(Settings::class.java)
            set.semStart = "04/12/2019"
            set.seg2End = "18/12/2019"
            set.seg3End = "25/12/2019"
            set.seg1End = "11/12/2019"
            realm.commitTransaction()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val ids = arrayOf(R.id.semStart, R.id.seg1, R.id.seg2, R.id.seg3)
        var s:TextView
        for(id in ids){
            s = view.findViewById(id)
            s.setOnClickListener(dateSetter(this.context!!, s))
        }
        view.findViewById<Button>(R.id.updateSem).setOnClickListener{
            if(isGreaterDate(seg1.text.toString(),seg2.text.toString()) || isGreaterDate(seg2.text.toString(),seg3.text.toString()) || isGreaterDate(semStart.text.toString(),seg1.text.toString())){
                Toast.makeText(this.context!!, "Segment dates are not in chronological order", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val set = realm.where(Settings::class.java).findFirst()!!
            realm.beginTransaction()
            set.semStart = semStart.text.toString()
            set.seg1End = seg1.text.toString()
            set.seg2End = seg2.text.toString()
            set.seg3End = seg3.text.toString()
            val a = realm.where(EachClass::class.java).notEqualTo("date", "").findAll()
            for(c in a){
                c.day = c.day.substring(0,4) + getSeg(c.date, set.semStart, set.seg1End, set.seg2End, set.seg3End)
            }
            realm.commitTransaction()
            Toast.makeText(this.context!!, "Updated", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.reset).setOnClickListener{
            val builder = AlertDialog.Builder(this.context!!)
            //set title for alert dialog
            builder.setTitle("Warning!")
            //set message for alert dialog
            builder.setMessage("This will delete all courses and their classes and cannot be undone!!")

            //performing positive action
                builder.setPositiveButton("DELETE EVERYTHING"){_, _ ->
                realm.beginTransaction()
                realm.where(EachClass::class.java).findAll().deleteAllFromRealm()
                realm.where(EachCourse::class.java).findAll().deleteAllFromRealm()
                realm.commitTransaction()
                Toast.makeText(this.context!!,"Reset successful!!",Toast.LENGTH_LONG).show()
            }
            builder.setNegativeButton("Cancel"){_,_ -> return@setNegativeButton}
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCanceledOnTouchOutside(true)
            alertDialog.show()
        }

        view.findViewById<Button>(R.id.exportData).setOnClickListener {
            if(Environment.MEDIA_MOUNTED != (Environment.getExternalStorageState()) || !checkPer(Manifest.permission.WRITE_EXTERNAL_STORAGE, context!!))return@setOnClickListener
            try {
                val file = File(Environment.getExternalStorageDirectory(), "c.txt")
                val fos = FileOutputStream(file)
                fos.write(1)
                fos.close()
                Toast.makeText(context, "Done!",Toast.LENGTH_LONG).show()

            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(context, "Failed!",Toast.LENGTH_LONG).show()
            }


        }

        view.findViewById<Button>(R.id.importData).setOnClickListener {

        }

        val set = realm.where(Settings::class.java).findFirst()!!
        view.findViewById<TextView>(R.id.semStart).text = set.semStart
        view.findViewById<TextView>(R.id.seg1).text = set.seg1End
        view.findViewById<TextView>(R.id.seg2).text = set.seg2End
        view.findViewById<TextView>(R.id.seg3).text = set.seg3End
        return view
    }

    private fun dateSetter(context:Context, view:TextView) : View.OnClickListener{
        return View.OnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, mnth, day ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, mnth)
                cal.set(Calendar.DAY_OF_MONTH, day)
                val mn = if(mnth < 9) "0"+(mnth+1) else (mnth+1).toString()
                val dy = if(day < 10) "0$day" else "$day"
                val mDate = String.format("%s/%s/%d", dy, mn, year)
                view.text = mDate
            }
            DatePickerDialog(
                context, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(
                    Calendar.DAY_OF_MONTH
                )
            ).show()
        }
    }

    private fun checkPer(per:String, context: Context):Boolean{
        return ContextCompat.checkSelfPermission(context, per) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
