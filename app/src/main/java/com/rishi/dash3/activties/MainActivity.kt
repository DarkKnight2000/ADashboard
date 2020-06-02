package com.rishi.dash3.activties

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.get
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rishi.dash3.R
import com.rishi.dash3.fragments.AllCourseFrag
import com.rishi.dash3.fragments.CalendarFragment
import com.rishi.dash3.fragments.Settings
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val cal = CalendarFragment()
    private val allC = AllCourseFrag()
    private val sets = Settings()
    private val tags = arrayOf("cal", "allC", "sets")
    private val titles = arrayOf("Calendar", "Courses", "Settings")
    private lateinit var listener: BottomNavigationView.OnNavigationItemSelectedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.i("test0", "${AppCompatDelegate.getDefaultNightMode()} ${AppCompatDelegate.MODE_NIGHT_NO} ${AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM}")


        listener = BottomNavigationView.OnNavigationItemSelectedListener{
            navToFrag(it.itemId)
        }


        botNav.setOnNavigationItemSelectedListener(listener)

        val realm = Realm.getDefaultInstance()
        if(Intent.ACTION_MAIN == intent.action && realm.where(com.rishi.dash3.models.Settings::class.java).findFirst() == null) {

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            startActivity(Intent(this, AppIntroActivity::class.java))

            realm.beginTransaction()
            val set = realm.createObject(com.rishi.dash3.models.Settings::class.java)
            set.semStart = "04/12/2019"
            set.seg2End = "18/12/2019"
            set.seg3End = "25/12/2019"
            set.seg1End = "11/12/2019"
            realm.commitTransaction()
            navToFrag(R.id.navigation_set)

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Warning!!")
            //set message for alert dialog
            builder.setMessage("Set the timetable start and end dates to continue.")

            //performing positive action
            builder.setPositiveButton("Ok"){_, _ ->  }
            //performing cancel action
            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCanceledOnTouchOutside(true)
            alertDialog.show()
        }
        else if(Intent.ACTION_SEND == intent.action && intent.type != null){
            if (intent.type!!.startsWith("text/")) {
                //Log.i("test0", intent.type + " " + intent.getParcelableExtra(Intent.EXTRA_STREAM))
                val inpUri = intent.getParcelableExtra(Intent.EXTRA_STREAM) as Uri

                val bundle = Bundle()
                bundle.putString("uri", inpUri.toString())
                sets.arguments = bundle


                navToFrag(R.id.navigation_set)

                Log.i("test0", "asdf")
            }
        }
        else{
            navToFrag(R.id.navigation_cal)
        }

        realm.close()

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

    fun navToFrag(itemId:Int):Boolean{
        val tool = supportActionBar
        val transaction = supportFragmentManager.beginTransaction()
        when (itemId) {
            R.id.navigation_cal -> {
                transaction.replace(R.id.frame_container, cal, tags[0])
                tool?.title = titles[0]
            }
            R.id.navigation_all -> {
                transaction.replace(R.id.frame_container, allC, tags[1])
                tool?.title = titles[1]
            }
            R.id.navigation_set -> {
                transaction.replace(R.id.frame_container, sets, tags[2])
                tool?.title = titles[2]
            }
            else -> {
                transaction.commit()
            }
        }
        transaction.addToBackStack(null)
        transaction.commit()
        return true
    }
}
