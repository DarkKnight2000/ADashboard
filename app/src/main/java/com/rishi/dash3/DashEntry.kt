package com.rishi.dash3

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.rishi.dash3.utils.ClsNotifChannelId
import io.realm.Realm
import io.realm.Realm.setDefaultConfiguration
import io.realm.RealmConfiguration


class DashEntry : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val realmConfig = RealmConfiguration.Builder()
            .name("dash.realm")
            .schemaVersion(0).deleteRealmIfMigrationNeeded()
            .build()
        setDefaultConfiguration(realmConfig)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(ClsNotifChannelId,"Class Notifications", NotificationManager.IMPORTANCE_HIGH)
            channel.description = "Channel for class notifications"
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

    }
}
