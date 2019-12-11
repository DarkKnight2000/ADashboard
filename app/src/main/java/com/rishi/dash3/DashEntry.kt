package com.rishi.dash3

import io.realm.Realm.setDefaultConfiguration
import io.realm.RealmConfiguration
import android.app.Application
import io.realm.Realm


class DashEntry : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val realmConfig = RealmConfiguration.Builder()
            .name("dash.realm")
            .schemaVersion(0).deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(realmConfig)
    }
}