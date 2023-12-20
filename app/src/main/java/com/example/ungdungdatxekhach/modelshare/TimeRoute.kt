package com.example.ungdungdatxekhach.modelshare

import java.util.Date

data class TimeRoute(var pickedHour: Int, var pickedMinute: Int) {
    constructor() : this(0, 0)
    fun addMinutes(minutesToAdd: Int): String {
        val newHour = pickedHour + minutesToAdd / 60
        val newMinute = (pickedMinute + minutesToAdd) % 60
        return newHour.toString() + ":" + newMinute.toString()
    }
}