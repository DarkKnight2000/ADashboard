package com.rishi.dash3

import io.realm.Realm.setDefaultConfiguration
import io.realm.RealmConfiguration
import android.app.Application
import com.rishi.dash3.Models.Settings
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
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val set = realm.createObject(Settings::class.java)
        set.semStart = "4/12/2019"
        set.seg2End = "18/12/2019"
        set.seg3End = "25/12/2019"
        set.seg1End = "11/12/2019"
        realm.commitTransaction()
        realm.close()
    }
}