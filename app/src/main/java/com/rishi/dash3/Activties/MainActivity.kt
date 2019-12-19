package com.rishi.dash3.Activties

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.get
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenu
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rishi.dash3.Fragments.AllCourseFrag
import com.rishi.dash3.Fragments.CalendarFragment
import com.rishi.dash3.Fragments.Settings
import com.rishi.dash3.R
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val cal = CalendarFragment()
    private val allC = AllCourseFrag()
    private val sets = Settings()
    private val tags = arrayOf("cal", "allC", "sets")
    private val titles = arrayOf("Calendar", "Courses", "Settings")
    lateinit var listener: BottomNavigationView.OnNavigationItemSelectedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        }
        realm.close()

        supportFragmentManager.beginTransaction().replace(R.id.frame_container, cal, tags[0]).commit()
        tool?.title = titles[0]

        listener = BottomNavigationView.OnNavigationItemSelectedListener{
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
        super.onBackPressed()
        Log.i("back_activity ", "here")
        var i = 0
        for(t in tags){
            var f = supportFragmentManager.findFragmentByTag(t)
            if(f!=null && f.isVisible){
                Log.i("back_activity ", "where$i")
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

    }
}
