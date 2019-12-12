package com.rishi.dash3.Activties

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.rishi.dash3.Adapters.InfoAdapter
import com.rishi.dash3.Models.EachClass
import com.rishi.dash3.Models.EachCourse
import com.rishi.dash3.R
import io.realm.Realm
import io.realm.RealmModel
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
        startTime.text = "11:20"
        endTime.text = "11:21"
        dateSelector.text  = "3/4/2019"



        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerViewClasses.layoutManager = layoutManager


        var realmObj: EachCourse ?= realm.where(EachCourse::class.java).equalTo("crsecode",textView3.text.toString()).findFirst()
        var presCls = (realm.copyFromRealm(realmObj!!.crseClsses))
        Toast.makeText(this,"Set",Toast.LENGTH_SHORT).show()

        val adapter = InfoAdapter(this, presCls, true, realm, realmObj)
        recyclerViewClasses.adapter = adapter


        var initID = getNextKey()
        btnAddCls.setOnClickListener {

            if(!dateSelector.text.contains("/") || !startTime.text.contains(":") || !endTime.text.contains(":")){
                Toast.makeText(this,"InValid Details",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //val clshes = realm.where(EachClass::class.java).between("start")

            val tempC = EachClass()
            tempC.id = initID++
            tempC.startTime = startTime.text.toString()
            tempC.endTime = endTime.text.toString()
            tempC.room = roomName.text.toString()
            if(weekly.isChecked){
                tempC.date = daySpinner.selectedItem.toString()
            }
            else{
                tempC.date = dateSelector.text.toString()
            }
            Toast.makeText(this, "id "+tempC.id, Toast.LENGTH_SHORT).show()
            presCls.add(tempC)
            val pos = presCls.indexOf(tempC)
            if (pos != -1) {
                val dataSize = presCls.size
                adapter.notifyItemInserted(dataSize)
                adapter.notifyItemRangeChanged(dataSize, dataSize)
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
                realmObj = realm.where(EachCourse::class.java).equalTo("crsecode",textView3.text.toString()).findFirst()
                realmObj?.crseClsses?.deleteAllFromRealm()
                realmObj?.crseClsses?.addAll(presCls)
                realm.commitTransaction()
                Toast.makeText(this,"Updated!!!",Toast.LENGTH_SHORT).show()
                for(c in  presCls) Log.i("Got tags2 ", c.id.toString())

            }catch (e: RealmException){
                Log.d("Tag",e.message)

            }
            //this.onDestroy()
        }
        weekly.isChecked = true
        dateSelector.visibility = View.GONE


        weekly.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                daySpinner.visibility = View.VISIBLE
                dateSelector.visibility = View.GONE
            }else{
                daySpinner.visibility = View.GONE
                dateSelector.visibility = View.VISIBLE
            }
        }

        val personNames = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, personNames)
        daySpinner.adapter = arrayAdapter

        daySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Toast.makeText(this@CourseInfo, "Selected " + personNames[position], Toast.LENGTH_SHORT).show()
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
                val mDate = String.format("%d/%d/%d",day,mnth+1,year)
                dateSelector.text = mDate
            }
            DatePickerDialog(this,dateSetListener,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()
        }


    }


    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun getNextKey(): Long {
        return try {
            val number = realm.where(EachClass::class.java).max("id")
            if (number != null) {
                number.toLong() + 1
            } else {
                0
            }
        } catch (e: ArrayIndexOutOfBoundsException) {
            0
        }
    }
}