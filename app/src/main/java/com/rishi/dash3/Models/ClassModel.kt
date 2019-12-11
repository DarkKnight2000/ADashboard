package com.rishi.dash3.Models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class EachClass:RealmObject(){
    @PrimaryKey
    var id: Long = -1
    var room: String = ""
    var startTime:String = ""
    var endTime:String = ""
    var date:String = ""
}