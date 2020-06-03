package com.rishi.dash3.activties

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.rishi.dash3.R
import com.rishi.dash3.adapters.InfoAdapter
import com.rishi.dash3.models.EachClass
import com.rishi.dash3.models.EachCourse
import com.rishi.dash3.models.Settings
import com.rishi.dash3.utils.*
import io.realm.Realm
import io.realm.RealmQuery
import io.realm.exceptions.RealmException
import kotlinx.android.synthetic.main.activity_courseinfo.*


class CourseInfo: AppCompatActivity(){

    lateinit var realm:Realm
    var edited = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courseinfo)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Course"
        realm = Realm.getDefaultInstance()

        val bundle :Bundle? = intent.extras
        textView2.text = bundle?.getString("name")
        textView3.text = bundle?.getString("code")
        textView6.text = bundle?.getString("slot")
        val calendar = Calendar.getInstance()
        dateSelector.text  = "Pick Date"
        var dateSelectedDay = calendar.get(Calendar.DAY_OF_WEEK)
        daySpinner.setSelection(calendar.get(Calendar.DAY_OF_WEEK) - 1)
        val settings = realm.where(Settings::class.java).findFirst()!!


        // startTime.text = intToTime(calendar.get(Calendar.HOUR_OF_DAY)*60+calendar.get(Calendar.MINUTE)+2)
        // endTime.text = intToTime(calendar.get(Calendar.HOUR_OF_DAY)*60+calendar.get(Calendar.MINUTE)+3)


        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerViewClasses.layoutManager = layoutManager


        var realmObj: EachCourse = realm.where(EachCourse::class.java).equalTo("crsecode",textView3.text.toString()).findFirst()!!
        var presCls = (realm.copyFromRealm(realmObj.crseClsses))
        //Toast.makeText(this,"Set",Toast.LENGTH_SHORT).show()

        val adapter = InfoAdapter(this, presCls, true, realm)
        recyclerViewClasses.adapter = adapter


        var initID = getNextKey(realm)
        btnAddCls.setOnClickListener {
            edited = true
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
            val errorMsg = getClshes(presCls + clshes.findAll(), tempC)
            if(errorMsg.isEmpty()){
                presCls.add(tempC)
                val pos = presCls.indexOf(tempC)
                if (pos != -1) {
                    val dataSize = presCls.size
                    adapter.notifyItemInserted(dataSize)
                    adapter.notifyItemRangeChanged(dataSize, dataSize)
                }
            }
            else{
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Oops!!")
                builder.setMessage(errorMsg)
                builder.setPositiveButton("Allow"){_,_ ->
                    presCls.add(tempC)
                    val pos = presCls.indexOf(tempC)
                    //Toast.makeText(this, "id "+tempC.id, Toast.LENGTH_SHORT).show()
                    if (pos != -1) {
                        val dataSize = presCls.size
                        adapter.notifyItemInserted(dataSize)
                        adapter.notifyItemRangeChanged(dataSize, dataSize)
                    }
                }
                builder.setNegativeButton("Cancel"){_,_ ->
                    initID--
                }
                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.setCanceledOnTouchOutside(true)
                alertDialog.show()
            }

            // startTime.text = intToTime(calendar.get(Calendar.HOUR_OF_DAY)*60+calendar.get(Calendar.MINUTE)+3)
            // endTime.text = intToTime(calendar.get(Calendar.HOUR_OF_DAY)*60+calendar.get(Calendar.MINUTE)+4)
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
                Toast.makeText(this,"Updated Classes :)",Toast.LENGTH_SHORT).show()
                if(realm.where(Settings::class.java).findFirst()!!.sendNotif) restartNotifService(
                    this
                )
                //for(c in  presCls) Log.i("Got tags2 ", c.id.toString())

            }catch (e: RealmException){
                Log.d("Tag",e.message)

            }
            //this.onDestroy()
            //startService(Intent(this, NotifService::class.java))
            this.finish()
        }
        weekly.isChecked = true
        dateSelector.visibility = View.GONE


        weekly.setOnCheckedChangeListener { _, isChecked ->
            edited = true
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
                Toast.makeText(this@CourseInfo, "Selected " + weekDays[position] +"day", Toast.LENGTH_SHORT).show()
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
            edited = true
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
            edited = true
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
            edited = true
            DatePickerDialog(this,dateSetListener,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Setting texts for segment choosing buttons
        radio_one.text = part1_code
        radio_two.text = part2_code
        radio_three.text = part3_code
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    override fun onBackPressed() {
        if(edited) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Warning!!")
            //set message for alert dialog
            builder.setMessage("Make sure you clicked the 'Update' button or else all your changes will be lost!")

            //performing positive action
            builder.setPositiveButton("Go back") { _, _ ->
                super.onBackPressed()
            }
            //performing cancel action
            builder.setNegativeButton("Stay") { _, _ ->
                //Toast.makeText(context,"Delete aborted",Toast.LENGTH_LONG).show()
            }
            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCanceledOnTouchOutside(true)
            alertDialog.show()
        }
        else{
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

}