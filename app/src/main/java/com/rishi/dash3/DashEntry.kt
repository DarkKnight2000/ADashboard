package com.rishi.dash3

import io.realm.Realm.setDefaultConfiguration
import io.realm.RealmConfiguration
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.rishi.dash3.notifications.ClsNotifChannelId
import io.realm.Realm


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
            val channel = NotificationChannel(ClsNotifChannelId,"Class Notifications", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Channel for class notifications"
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
