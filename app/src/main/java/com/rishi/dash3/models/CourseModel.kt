package com.rishi.dash3.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class EachCourse:RealmObject(){

    @PrimaryKey
    var crsecode: String = ""
    var crsename: String = ""
    var defSlot: String = ""
    var crseClsses:RealmList<EachClass> = RealmList()
}
