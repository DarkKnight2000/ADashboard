package com.rishi.dash3

fun dateToInt(date:String):Int{
    val form = date.split(":")
    var res = 0
    res += form[0].toInt()*60
    res += form[1].toInt()
    return res
}

fun intToDate(value:Int):String{
    var str = ""
    str += (value/60)
    str += ":"
    str += value.rem(60)
    return str
}