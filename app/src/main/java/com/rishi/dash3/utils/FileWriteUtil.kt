package com.rishi.dash3.utils

import android.util.JsonReader
import android.util.JsonToken
import android.util.JsonWriter
import android.util.Log
import com.rishi.dash3.models.EachClass
import com.rishi.dash3.models.EachCourse
import com.rishi.dash3.models.Settings
import io.realm.Realm
import io.realm.RealmList
import java.io.OutputStream
import java.io.OutputStreamWriter


fun writeCourses(oos : OutputStream, realm:Realm){
    val writer = JsonWriter(OutputStreamWriter(oos, "UTF-8"))
    writer.setIndent("  ")

    writer.beginObject()
    writeSettings(writer, realm)

    writer.name("Courses")
    writer.beginArray()
    for(fcr: EachCourse in realm.where(EachCourse::class.java).findAll()){
        writeEachCourse(writer, fcr)
    }
    writer.endArray()
    writer.endObject()
    writer.close()

}

fun writeSettings(writer: JsonWriter, realm:Realm){

    val sets = realm.where(Settings::class.java).findFirst()!!

    writer.name("Settings")
    writer.beginObject()
    writer.name("p1").value(sets.semStart)
    writer.name("p2").value(sets.seg1End)
    writer.name("p3").value(sets.seg2End)
    writer.name("p4").value(sets.seg3End)
    writer.name("notif").value(sets.sendNotif)
    writer.endObject()
}

fun writeEachCourse(writer: JsonWriter, eachCourse: EachCourse){
    writer.beginObject()

    writer.name("code").value(eachCourse.crsecode)
    writer.name("name").value(eachCourse.crsename)
    writer.name("slot").value(eachCourse.defSlot)

    writer.name("classes")
    writer.beginArray()
    for(fcls: EachClass in eachCourse.crseClsses){
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

fun readAllCourses(reader: JsonReader):List<EachCourse>{
    val readCrses:ArrayList<EachCourse> = arrayListOf()

    Log.i("test0", reader.nextName())
    reader.beginArray()
    while(reader.hasNext()){
        readCrses.add(readEachCourse(reader))
    }

    reader.endArray()
    return readCrses
}

fun readEachCourse(reader: JsonReader): EachCourse {
    val newCrse = EachCourse()
    reader.beginObject()
    while(reader.hasNext()){
        val name = reader.nextName()
        if (name == "code") {
            newCrse.crsecode = reader.nextString()
        } else if (name == "name") {
            newCrse.crsename = reader.nextString()
        } else if (name == "classes" && reader.peek() !== JsonToken.NULL) {
            newCrse.crseClsses = readClassArray(reader)
        } else if (name == "slot") {
            newCrse.defSlot = reader.nextString()
        } else {
            reader.skipValue()
        }
    }
    reader.endObject()
    return newCrse
}

fun readClassArray(reader: JsonReader):RealmList<EachClass>{
    val readClsses = RealmList<EachClass>()

    reader.beginArray()
    while(reader.hasNext()){
        readClsses.add(readEachClass(reader))
    }
    reader.endArray()
    return readClsses
}

fun readEachClass(reader: JsonReader): EachClass {
    val newCls = EachClass()

    reader.beginObject()
    while(reader.hasNext()){
        val name = reader.nextName()
        if (name == "code") {
            newCls.code = reader.nextString()
        } else if (name == "id") {
            newCls.id = reader.nextLong()
        } else if (name == "date") {
            newCls.date = reader.nextString()
        } else if (name == "day") {
            newCls.day = reader.nextString()
        } else if (name == "start") {
            newCls.startTime = reader.nextInt()
        } else if (name == "end") {
            newCls.endTime = reader.nextInt()
        } else if (name == "room") {
            newCls.room = reader.nextString()
        } else {
            reader.skipValue()
        }
    }
    reader.endObject()
    return newCls
}

fun readSettings(reader: JsonReader): Settings {

    val sets = Settings()

    reader.beginObject()
    Log.i("test0", reader.nextName())
    reader.beginObject()
    while(reader.hasNext()){
        val name = reader.nextName()
        if (name == "p1") {
            sets.semStart = reader.nextString()
        } else if (name == "p2") {
            sets.seg1End = reader.nextString()
        } else if (name == "p3") {
            sets.seg2End = reader.nextString()
        } else if (name == "p4") {
            sets.seg3End = reader.nextString()
        } else if (name == "notif") {
            sets.sendNotif = reader.nextBoolean()
        } else {
            reader.skipValue()
        }
    }
    reader.endObject()
    return sets
}


















