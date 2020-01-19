package com.rishi.dash3.activties

import android.app.PendingIntent
import android.content.Intent
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.get
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rishi.dash3.fragments.AllCourseFrag
import com.rishi.dash3.fragments.CalendarFragment
import com.rishi.dash3.fragments.Settings
import com.rishi.dash3.R
import com.rishi.dash3.services.NotifService
import com.rishi.dash3.services.SensorRestarter
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val cal = CalendarFragment()
    private val allC = AllCourseFrag()
    private val sets = Settings()
    private val tags = arrayOf("cal", "allC", "sets")
    private val titles = arrayOf("Calendar", "Courses", "Settings")
    private lateinit var listener: BottomNavigationView.OnNavigationItemSelectedListener
    private var exit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        setContentView(R.layout.activity_main)
        val tool = supportActionBar

        val realm = Realm.getDefaultInstance()
        if(realm.where(com.rishi.dash3.Models.Settings::class.java).findFirst() == null) {
            realm.beginTransaction()
            val set = realm.createObject(com.rishi.dash3.Models.Settings::class.java)
            set.semStart = "04/12/2019"
            set.seg2End = "18/12/2019"
            set.seg3End = "25/12/2019"
            set.seg1End = "11/12/2019"
            realm.commitTransaction()
            supportFragmentManager.beginTransaction().replace(R.id.frame_container, sets, tags[2]).commit()
            botNav.selectedItemId = R.id.navigation_set
            tool?.title = titles[2]
            Toast.makeText(this, "Set semester dates to get started", Toast.LENGTH_LONG).show()
        }
        else{
            supportFragmentManager.beginTransaction().replace(R.id.frame_container, cal, tags[0]).commit()
            tool?.title = titles[0]
        }


        realm.close()

        listener = BottomNavigationView.OnNavigationItemSelectedListener{
            exit = false
            val transaction = supportFragmentManager.beginTransaction()
            if(it.itemId == R.id.navigation_cal) {
                transaction.replace(R.id.frame_container, cal, tags[0])
                tool?.title = titles[0]
            }
            else if(it.itemId == R.id.navigation_all) {
                transaction.replace(R.id.frame_container, allC, tags[1])
                tool?.title = titles[1]
            }
            else if(it.itemId == R.id.navigation_set) {
                transaction.replace(R.id.frame_container, sets, tags[2])
                tool?.title = titles[2]
            }
            else{
                transaction.commit()
            }
            transaction.addToBackStack(null)
            transaction.commit()
            true
        }


        botNav.setOnNavigationItemSelectedListener(listener)

    }

    override fun onBackPressed() {
        //Log.i("back_activity ", "here")
        super.onBackPressed()
        var i = 0
        for(t in tags){
            val f = supportFragmentManager.findFragmentByTag(t)
            if(f!=null && f.isVisible){
                //Log.i("back_activity ", "where$i")
                botNav.setOnNavigationItemSelectedListener{
                    true
                }
                botNav.selectedItemId = botNav.menu[i].itemId
                supportActionBar?.title = titles[i]
                botNav.setOnNavigationItemSelectedListener(listener)
                //Toast.makeText(this, "active frag "+i, Toast.LENGTH_SHORT).show()
                break
            }
            ++i
        }
/*        if(exit) super.onBackPressed()
        else{
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
            exit = true
        }*/

    }
}
