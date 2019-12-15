package com.rishi.dash3.Models

import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey


open class EachClass:RealmObject(){
    @PrimaryKey
    var id: Long = -1
    var room: String = ""
    var startTime:Int = -1
    var endTime:Int = -1
    var date:String = ""
    var day:String = ""
    var code:String = ""
}