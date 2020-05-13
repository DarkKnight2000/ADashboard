package com.rishi.dash3.models

import android.util.JsonWriter
import io.realm.Realm
import java.io.OutputStream
import java.io.OutputStreamWriter

fun writeCourses(oos : OutputStream, realm:Realm){
    val writer = JsonWriter(OutputStreamWriter(oos, "UTF-8"))
    writer.setIndent("  ");

    writer.beginArray()
    for(fcr:EachCourse in realm.where(EachCourse::class.java).findAll()){
        writeEachCourse(writer, fcr)
    }
    writer.endArray()
    writer.close()

}

fun writeEachCourse(writer: JsonWriter, eachCourse: EachCourse){
    writer.beginObject()

    writer.name("code").value(eachCourse.crsecode)
    writer.name("name").value(eachCourse.crsename)
    writer.name("slot").value(eachCourse.defSlot)

    writer.name("classes")
    writer.beginArray()
    for(fcls:EachClass in eachCourse.crseClsses){
        writeEachClass(writer, fcls)
    }
    writer.endArray()

    writer.endObject()
}

fun writeEachClass(writer: JsonWriter, eachClass: EachClass){
    writer.beginObject()

    writer.name("id").value(eachClass.id)
    writer.name("code").value(eachClass.code)
    writer.name("date").value(eachClass.date)
    writer.name("day").value(eachClass.day)
    writer.name("start").value(eachClass.startTime)
    writer.name("end").value(eachClass.endTime)
    writer.name("room").value(eachClass.room)

    writer.endObject()
}