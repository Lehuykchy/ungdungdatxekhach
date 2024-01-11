package com.example.ungdungdatxekhach.modelshare

import java.io.Serializable
import java.util.Date

data class TimeRoute(var pickedHour: Int, var pickedMinute: Int) : Serializable {
    constructor() : this(0, 0)
    fun addMinutes(minutesToAdd: Int): String {
        val newHour = pickedHour + minutesToAdd / 60
        val newMinute = (pickedMinute + minutesToAdd) % 60
        return newHour.toString() + ":" + newMinute.toString()
    }
    fun toFormattedString(): String {
        val hourString = if (pickedHour < 10) "0$pickedHour" else pickedHour.toString()
        val minuteString = if (pickedMinute < 10) "0$pickedMinute" else pickedMinute.toString()

        return "$hourString$minuteString"
    }

    fun toFormattString(): String {
        val hourString = if (pickedHour < 10) "0$pickedHour" else pickedHour.toString()
        val minuteString = if (pickedMinute < 10) "0$pickedMinute" else pickedMinute.toString()

        return "$hourString:$minuteString"
    }

}