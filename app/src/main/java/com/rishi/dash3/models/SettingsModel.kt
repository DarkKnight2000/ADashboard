package com.rishi.dash3.models


import io.realm.RealmObject


open class Settings:RealmObject() {

    var id = 0
    public var semStart = "Select Date"
    public var seg1End = "Select Date"
    public var seg2End = "Select Date"
    public var seg3End = "Select Date"
    var sendNotif = false
}
