package com.rishi.dash3.activties

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import com.rishi.dash3.R


class AppIntroActivity : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        addSlide(AppIntroFragment.newInstance("Timetable app", "A simple app to keep track of all your classes.", R.mipmap.ic_launcher, Color.DKGRAY))
        addSlide(AppIntroFragment.newInstance("All your courses at one place", "Update your classes at any time", R.mipmap.ic_launcher, Color.DKGRAY))
        addSlide(AppIntroFragment.newInstance("Manage different timetables", "Divide your calendar into 3 parts and add courses into any or all parts.", R.mipmap.ic_launcher, Color.DKGRAY))
        addSlide(AppIntroFragment.newInstance("Alerts!", "Get notifications before every class!", R.drawable.notif, Color.DKGRAY))
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Do something when users tap on Done button.
        finish()
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        // Do something when users tap on Skip button.
        finish()
    }
}
