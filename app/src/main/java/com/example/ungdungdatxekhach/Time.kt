package com.example.ungdungdatxekhach

data class Time(var pickedHour: Int, var pickedMinute: Int) {
    constructor() : this(0, 0)
}