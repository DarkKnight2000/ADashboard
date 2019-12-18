package com.rishi.dash3

import com.rishi.dash3.Fragments.Settings
import com.rishi.dash3.Models.EachClass
import io.realm.Realm
import java.text.SimpleDateFormat
import java.util.*

fun dateToInt(date:String):Int{
    val form = date.split(":")
    var res = 0
    res += form[0].toInt()*60
    res += form[1].toInt()
    return res
}

fun intToDate(value:Int):String{
    var str = ""
    str += if (value/60 < 10) "0"+(value/60) else (value/60)
    str += ":"
    str += if (value.rem(60) < 10) "0"+(value.rem(60)) else (value.rem(60))
    return str
}

val sdf = SimpleDateFormat("dd/MM/yyyy")

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

fun getSeg(a:String, s1:String, s2:String, s3:String, s4:String):String{
    if(!isGreaterDate(a,s1)) return ""
    else if(!isGreaterDate(a,s2)) return "1-2"
    else if(!isGreaterDate(a,s3)) return "3-4"
    else if(!isGreaterDate(a,s4)) return "5-6"
    else return ""
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