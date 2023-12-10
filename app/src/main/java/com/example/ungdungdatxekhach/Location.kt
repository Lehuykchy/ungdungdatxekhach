package com.example.ungdungdatxekhach

data class Location (var city : String, var district: String, var ward: String, var other: String){
    constructor() : this("", "", "", "")
}