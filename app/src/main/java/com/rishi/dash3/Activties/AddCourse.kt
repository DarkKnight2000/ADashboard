package com.rishi.dash3.Activties


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.rishi.dash3.*
import com.rishi.dash3.Models.EachClass
import com.rishi.dash3.Models.EachCourse
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_add_course.*

class AddCourse : AppCompatActivity() {

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_add_course)
        actionBar?.title = "Course Info"
        realm = Realm.getDefaultInstance()

        // Inflate the layout for this fragment
        Log.i("Hey","view")
        val name = findViewById<EditText>(R.id.textView2)
        val code = findViewById<EditText>(R.id.textView3)
        val room = findViewById<EditText>(R.id.room)
        val slotSel = findViewById<Spinner>(R.id.slotSel)
        val segSel = findViewById<Spinner>(R.id.segSel)
        //val preCls= findViewById<TextView>(R.id.defClses)
        val classMap:HashMap<String, ArrayList<String>> = HashMap()
        classMap["A"] =  arrayListOf("Mon 09:00 10:00", "Wed 11:00 12:00", "Thu 10:00 11:00")
        classMap["B"] = arrayListOf("Tue 09:00 10:00")
        classMap["None"] = arrayListOf()
        val segMap:HashMap<String, ArrayList<String>> = HashMap()
        segMap["1-2"] =  arrayListOf("1-2")
        segMap["1-4"] = arrayListOf("1-2","3-4")
        val preSlots = classMap.keys.toTypedArray()
        val preSegs = segMap.keys.toTypedArray()

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, preSlots)
        slotSel.adapter = arrayAdapter

        slotSel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Toast.makeText(this@AddCourse, "Selected " + preSlots[position], Toast.LENGTH_SHORT).show()
                val arr = classMap[preSlots[position]]!!
                var str = ""
                for(a in arr) str += a + "\n"
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
            var initID = getNextKey(realm)
            var clsesCheck = arrayListOf<EachClass>()
            val clses = defClses.text.split("\n")
            val prev = realm.where(EachClass::class.java).findAll()
            for(s in segMap[segSel.selectedItem]!!.toTypedArray()) {
                for (c in clses) {
                    val cls = EachClass()
                    cls.id = initID++
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
                    clsesCheck.add(cls)
                }
            }
            realm.beginTransaction()
            val crse = realm.createObject(EachCourse::class.java, code.text.toString())
            crse.crsename = name.text.toString()
            crse.defSlot = preSlots[slotSel.selectedItemPosition]
            crse.crseClsses.addAll(clsesCheck)
            realm.commitTransaction()
            this.finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
