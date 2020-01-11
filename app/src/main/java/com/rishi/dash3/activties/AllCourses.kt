package com.rishi.dash3.activties

import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm

class AllCourses : AppCompatActivity(){
    lateinit var realm: Realm

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_allcourses)
        realm = Realm.getDefaultInstance()

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager


        var allCrs = realm.where(EachCourse::class.java).findAll()
        var adapter = ClassesAdapter(this, allCrs, realm)
        recyclerView.adapter = adapter

        crseAdd.setOnClickListener {
            realm.beginTransaction()
            realm.deleteAll()
            var newCrse = realm.createObject(EachCourse::class.java, "C")
            newCrse.crsename = "N"
            newCrse.defSlot = "S"
            var tempcls = EachClass()
            tempcls.endTime = 750
            tempcls.startTime = 720
            tempcls.room = "A-LH1"
            tempcls.date = "12/12/2019"
            tempcls.day = "Thu"
            tempcls.id = 0
            newCrse.crseClsses.add(tempcls)
            realm.commitTransaction()
            allCrs = realm.where(EachCourse::class.java).findAll()
            adapter = ClassesAdapter(this, allCrs, realm)
            recyclerView.adapter = adapter
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }*/
}