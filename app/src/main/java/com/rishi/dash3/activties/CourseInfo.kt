package com.rishi.dash3.activties

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.rishi.dash3.*
import com.rishi.dash3.adapters.InfoAdapter
import com.rishi.dash3.Models.EachClass
import com.rishi.dash3.Models.EachCourse
import com.rishi.dash3.Models.Settings
import com.rishi.dash3.services.NotifService
import io.realm.Realm
import io.realm.RealmQuery
import io.realm.exceptions.RealmException
import kotlinx.android.synthetic.main.activity_courseinfo.*


class CourseInfo: AppCompatActivity(){

    lateinit var realm:Realm


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courseinfo)
        realm = Realm.getDefaultInstance()

        val bundle :Bundle? = intent.extras
        textView2.text = bundle?.getString("name")
        textView3.text = bundle?.getString("code")
        textView6.text = bundle?.getString("slot")
        startTime.text = "Pick Time"
        endTime.text = "Pick Time"
        dateSelector.text  = "Pick Date"
        var dateSelectedDay = 7
        val weekDays = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val settings = realm.where(Settings::class.java).findFirst()!!


        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerViewClasses.layoutManager = layoutManager


        var realmObj: EachCourse = realm.where(EachCourse::class.java).equalTo("crsecode",textView3.text.toString()).findFirst()!!
        var presCls = (realm.copyFromRealm(realmObj.crseClsses))
        Toast.makeText(this,"Set",Toast.LENGTH_SHORT).show()

        val adapter = InfoAdapter(this, presCls, true, realm, realmObj)
        recyclerViewClasses.adapter = adapter


        var initID = getNextKey(realm)
        btnAddCls.setOnClickListener {

            if((!weekly.isChecked && !dateSelector.text.contains("/"))|| !startTime.text.contains(":") || !endTime.text.contains(":")){
                Toast.makeText(this,"Invalid Details",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if(timeToInt(startTime.text.toString()) >= timeToInt(endTime.text.toString())){
                Toast.makeText(this,"Class start time cannot be less than End time",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val tempC = EachClass()
            tempC.id = initID++
            tempC.startTime = timeToInt(startTime.text.toString())
            tempC.endTime = timeToInt(endTime.text.toString())
            tempC.room = roomName.text.toString()
            tempC.code = textView3.text.toString()
            var clshes:RealmQuery<EachClass>
            var clshPres:EachClass
            if(weekly.isChecked){
                tempC.day = daySpinner.selectedItem.toString() + " " + findViewById<RadioButton>(segSelector.checkedRadioButtonId).text
                tempC.date = ""
                clshes = realm.where(EachClass::class.java).notEqualTo("code", tempC.code).equalTo("day", tempC.day)
            }
            else{
                tempC.date = dateSelector.text.toString()
                tempC.day = weekDays[dateSelectedDay-1]  + " " + getSeg(tempC.date, settings.semStart, settings.seg1End, settings.seg2End, settings.seg3End)
                clshes = realm.where(EachClass::class.java).notEqualTo("code", tempC.code).equalTo("day", tempC.day)
                val c1 = clshes.`in`("date", arrayOf("", tempC.date))
                clshes = c1
            }
            val clshes1 = clshes.between("startTime", tempC.startTime, tempC.endTime-1)
            val clshes2 = clshes.between("endTime", tempC.startTime+1, tempC.endTime)

            var clshCls = clshes1.findFirst()
            if(clshCls == null){
                clshCls = clshes2.findFirst()
            }
            clshPres = getClshes(presCls, tempC)
            if(clshCls == null && clshPres.id == (-1).toLong()){
                //Toast.makeText(this, "id "+tempC.id, Toast.LENGTH_SHORT).show()
                presCls.add(tempC)
                val pos = presCls.indexOf(tempC)
                if (pos != -1) {
                    val dataSize = presCls.size
                    adapter.notifyItemInserted(dataSize)
                    adapter.notifyItemRangeChanged(dataSize, dataSize)
                }
            }
            else{
                initID--
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Oops!!")
                val msg =  if(clshCls!=null)"Clashing with class of " + clshCls.code + " from ${intToTime(clshCls.startTime)} to ${intToTime(clshCls.endTime)}"
                else "Clashing with class of " + clshPres.code + " from ${intToTime(clshPres.startTime)} to ${intToTime(clshPres.endTime)}"
                builder.setMessage(msg)
                builder.setPositiveButton("OK"){_,_ -> return@setPositiveButton}
                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.setCanceledOnTouchOutside(true)
                alertDialog.show()
            }
        }



        btnCommit.setOnClickListener {

            try{
                realm.beginTransaction()
                /*for (c:EachClass in presCls){
                    if(!realmObj.crseClsses.contains(c)) realmObj.crseClsses.add(c)
                }
                var temp = RealmList<EachClass>()
                temp.addAll(realmObj.crseClsses)
                for (c:EachClass in temp){
                    if(!presCls.contains(c)) realmObj.crseClsses.remove(c)
                }*/
                realmObj = realm.where(EachCourse::class.java).equalTo("crsecode",textView3.text.toString()).findFirst()!!
                realmObj.crseClsses.deleteAllFromRealm()
                realmObj.crseClsses.addAll(presCls)
                realm.commitTransaction()
                Toast.makeText(this,"Updated!!!",Toast.LENGTH_SHORT).show()
                //for(c in  presCls) Log.i("Got tags2 ", c.id.toString())

            }catch (e: RealmException){
                Log.d("Tag",e.message)

            }
            //this.onDestroy()
            startService(Intent(this, NotifService::class.java))
            this.finish()
        }
        weekly.isChecked = true
        dateSelector.visibility = View.GONE


        weekly.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                daySpinner.visibility = View.VISIBLE
                segSelector.visibility = View.VISIBLE
                dateSelector.visibility = View.GONE
            }else{
                daySpinner.visibility = View.GONE
                segSelector.visibility = View.GONE
                dateSelector.visibility = View.VISIBLE
            }
        }


        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, weekDays)
        daySpinner.adapter = arrayAdapter

        daySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Toast.makeText(this@CourseInfo, "Selected " + weekDays[position], Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code to perform some action when nothing is selected
            }
        }



        startTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY,hour)
                cal.set(Calendar.MINUTE,minute)
                startTime.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(this,timeSetListener,cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),true).show()
        }

        endTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->

                cal.set(Calendar.HOUR_OF_DAY,hour)
                cal.set(Calendar.MINUTE,minute)
                val time = SimpleDateFormat("HH:mm").format(cal.time)
                if(time > startTime.text.toString())
                    endTime.text = time
                else
                    Toast.makeText(this,"End time cant be ahead of Start time",Toast.LENGTH_SHORT).show()
            }
            TimePickerDialog(this,timeSetListener,cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),true).show()
        }

        dateSelector.setOnClickListener{
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, mnth, day ->
                cal.set(Calendar.YEAR,year)
                cal.set(Calendar.MONTH,mnth)
                cal.set(Calendar.DAY_OF_MONTH,day)
                val mn = if(mnth < 10) "0"+(mnth+1) else (mnth+1).toString()
                val dy = if(day < 10) "0$day" else "$day"
                val mDate = String.format("%s/%s/%d", dy, mn, year)
                dateSelector.text = mDate
                dateSelectedDay = cal.get(Calendar.DAY_OF_WEEK)
            }
            DatePickerDialog(this,dateSetListener,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

}