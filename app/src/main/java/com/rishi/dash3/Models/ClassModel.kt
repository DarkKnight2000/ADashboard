package com.rishi.dash3.Models

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import kotlinx.android.parcel.Parcelize

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