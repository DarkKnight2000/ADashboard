package com.rishi.dash3.Activties

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rishi.dash3.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calendar.setOnClickListener {

            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)

        }
        btnAll.setOnClickListener{
            val intent = Intent(this, AllCourses::class.java)
            startActivity(intent)
        }

        // TODO: Need to add course adding page

    }
}
