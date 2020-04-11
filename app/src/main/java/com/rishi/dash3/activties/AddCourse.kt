package com.rishi.dash3.activties


//import com.rishi.dash3.services.NotifService

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.rishi.dash3.*
import com.rishi.dash3.Models.EachClass
import com.rishi.dash3.Models.EachCourse
import com.rishi.dash3.Models.Settings
import com.rishi.dash3.notifications.restartNotifService
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_add_course.*


class AddCourse : AppCompatActivity() {

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add Course"
        realm = Realm.getDefaultInstance()

        // Inflate the layout for this fragment
        //Log.i("Hey","view")
        val name = findViewById<EditText>(R.id.textView2)
        val code = findViewById<EditText>(R.id.textView3)
        val room = findViewById<EditText>(R.id.room)
        val slotSel = findViewById<Spinner>(R.id.slotSel)
        val segSel = findViewById<Spinner>(R.id.segSel)
        //val preCls= findViewById<TextView>(R.id.defClses)
        val classMap:HashMap<String, ArrayList<String>> = HashMap()
        classMap["--"] = arrayListOf()
        classMap["A"] = arrayListOf("Mon 09:00 10:00", "Wed 11:00 12:00", "Thu 10:00 11:00")
        classMap["B"] = arrayListOf("Mon 10:00 11:00", "Wed 09:00 10:00", "Thu 11:00 12:00")
        classMap["C"] = arrayListOf("Mon 11:00 12:00", "Wed 10:00 11:00", "Thu 09:00 10:00")
        classMap["D"] = arrayListOf("Mon 12:00 13:00", "Tue 09:00 10:00", "Fri 11:00 12:00")
        classMap["E"] = arrayListOf("Tue 10:00 11:00", "Thu 12:00 13:00", "Fri 09:00 10:00")
        classMap["F"] = arrayListOf("Tue 11:00 12:00", "Wed 14:30 15:30", "Fri 10:00 11:00")
        classMap["G"] = arrayListOf("Tue 12:00 13:00", "Wed 12:00 13:00", "Fri 12:00 13:00")
        classMap["P"] = arrayListOf("Mon 14:30 16:00", "Thu 16:00 17:30")
        classMap["Q"] = arrayListOf("Mon 16:00 17:30", "Thu 14:30 16:00")
        classMap["R"] = arrayListOf("Tue 14:30 16:00", "Fri 16:00 17:30")
        classMap["S"] = arrayListOf("Tue 16:00 17:30", "Fri 14:30 16:00")
        classMap["W"] = arrayListOf("Mon 17:30 19:00", "Thu 17:30 19:00")
        classMap["X"] = arrayListOf("Mon 19:00 20:30", "Thu 19:00 20:30")
        classMap["Y"] = arrayListOf("Tue 17:30 19:00", "Fri 17:30 19:00")
        classMap["Z"] = arrayListOf("Tue 19:00 20:30", "Fri 19:00 20:30")
        val segMap:HashMap<String, ArrayList<String>> = HashMap()
        segMap["1"] =  arrayListOf(part1_code)
        segMap["1-2"] = arrayListOf(part1_code,part2_code)
        segMap["1-3"] =  arrayListOf(part1_code,part2_code,part3_code)
        segMap["2"] = arrayListOf(part2_code)
        segMap["2-3"] = arrayListOf(part2_code,part3_code)
        segMap["3"] = arrayListOf(part3_code)
        val preSlots = classMap.keys.toTypedArray()
        val preSegs = segMap.keys.toTypedArray()

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, preSlots)
        slotSel.adapter = arrayAdapter

        slotSel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Toast.makeText(this@AddCourse, "Selected " + preSlots[position], Toast.LENGTH_SHORT).show()
                val arr = classMap[preSlots[position]]
                var str = ""
                if(arr!= null && arr.size != 0) for(a in arr) str += a + "\n"
                else str = "-"
                defClses.text = str
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code to perform some action when nothing is selected
            }
        }

        val arrayAdapter2 = ArrayAdapter(this, android.R.layout.simple_list_item_1, preSegs)
        segSel.adapter = arrayAdapter2

        findViewById<Button>(R.id.addCrse).setOnClickListener {
            if(name.text.toString() == "" || code.text.toString() == "" || room.text.toString() == ""){
                Toast.makeText(this, "Fill all details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if(realm.where(EachCourse::class.java).equalTo("crsecode", code.text.toString()).findFirst() != null){
                Toast.makeText(this, "A course with same code exists!! Try another code or edit that course", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var initID = getNextKey(realm)
            var clsesCheck = arrayListOf<EachClass>()
            val clses = defClses.text.split("\n")
            val prev = realm.where(EachClass::class.java).findAll()
            for(s in segMap[segSel.selectedItem]!!.toTypedArray()) {
                for (c in clses) {
                    val cls = EachClass()
                    val c1 = c.split(" ")
                    if (c1.size < 3) continue
                    cls.startTime = timeToInt(c1[1])
                    cls.endTime = timeToInt(c1[2])
                    cls.room = room.text.toString()
                    cls.day = c1[0] + " " + s
                    cls.code = code.text.toString()
                    val t = getClshes(prev, cls)
                    if(t.id != (-1).toLong()){
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Oops!!")
                        val msg =  "Clashing with class of " + t.code + " from ${intToTime(t.startTime)} to ${intToTime(t.endTime)}"
                        builder.setMessage(msg)
                        builder.setPositiveButton("OK"){_,_ -> return@setPositiveButton}
                        val alertDialog: AlertDialog = builder.create()
                        alertDialog.setCancelable(false)
                        alertDialog.setCanceledOnTouchOutside(true)
                        alertDialog.show()
                        return@setOnClickListener
                    }
                    cls.id = initID++
                    clsesCheck.add(cls)
                }
            }
            realm.beginTransaction()
            val crse = realm.createObject(EachCourse::class.java, code.text.toString())
            crse.crsename = name.text.toString()
            crse.defSlot = preSlots[slotSel.selectedItemPosition]
            crse.crseClsses.addAll(clsesCheck)
            realm.commitTransaction()
            if(clsesCheck.isNotEmpty() && realm.where(Settings::class.java).findFirst()!!.sendNotif){
                restartNotifService(this)
            }
            //startService(Intent(this, NotifService::class.java))
            Toast.makeText(this,"Added Course to your list :)",Toast.LENGTH_SHORT).show()
            this.finish()
        }

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

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Warning!!")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to go back? All your changes will be lost!")

        //performing positive action
        builder.setPositiveButton("Go back"){_, _ ->
            super.onBackPressed()
        }
        //performing cancel action
        builder.setNeutralButton("Stay"){_ , _ ->
            //Toast.makeText(context,"Delete aborted",Toast.LENGTH_LONG).show()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCanceledOnTouchOutside(true)
        alertDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
