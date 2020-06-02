package com.rishi.dash3.utils

import com.rishi.dash3.models.EachClass
import com.rishi.dash3.models.Settings
import io.realm.Realm
import java.text.SimpleDateFormat
import java.util.*

fun timeToInt(time:String):Int{
    val form = time.split(":")
    var res = 0
    res += form[0].toInt()*60
    res += form[1].toInt()
    return res
}

fun intToTime(value:Int):String{
    var str = ""
    str += if (value/60 < 10) "0"+(value/60) else (value/60)
    str += ":"
    str += if (value.rem(60) < 10) "0"+(value.rem(60)) else (value.rem(60))
    return str
}

val sdf = SimpleDateFormat("dd/MM/yyyy")

val weekDays = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

fun isGreaterDate(a:String, b:String):Boolean{
    val ad: Date
    try {
        ad = sdf.parse(a)
    }catch (e:Exception){
        return true
    }
    val bd: Date
    try {
        bd = sdf.parse(b)
    }catch (e:Exception){
        return true
    }
    if(bd.after(ad)) return false
    return true
}

const val part1_code = "1"
const val part2_code = "2"
const val part3_code = "3"

fun getSeg(a:String, s1:String, s2:String, s3:String, s4:String):String{
    return if(!isGreaterDate(a, s1)) ""
    else if(!isGreaterDate(
            a,
            s2
        )
    ) part1_code
    else if(!isGreaterDate(
            a,
            s3
        )
    ) part2_code
    else if(!isGreaterDate(
            a,
            s4
        )
    ) part3_code
    else ""
}

fun getSeg(a:String, s:Settings):String{
    return getSeg(
        a,
        s.semStart,
        s.seg1End,
        s.seg2End,
        s.seg3End
    )
}


fun getNextKey(realm: Realm): Long {
    return try {
        val number = realm.where(EachClass::class.java).max("id")
        if (number != null) {
            number.toLong() + 1
        } else {
            0
        }
    } catch (e: ArrayIndexOutOfBoundsException) {
        0
    }
}


fun getClshes(a:List<EachClass>, b:EachClass):EachClass{
    for(c:EachClass in a){
        if(c.day == b.day && ((c.startTime <= b.startTime && b.startTime < c.endTime ) || (c.startTime < b.endTime && b.endTime <= c.endTime ))){
            if(c.date == "") return c
            else if(c.date == b.date) return c
        }
    }
    return EachClass()
}