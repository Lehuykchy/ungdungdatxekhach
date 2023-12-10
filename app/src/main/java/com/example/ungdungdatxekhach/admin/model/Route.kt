package com.example.ungdungdatxekhach.admin.model

import com.example.ungdungdatxekhach.Location
import com.example.ungdungdatxekhach.Time

data class Route(
    var id: String,
    var idAdmin: String,
    var departureLocation: String,
    var destination: String,
    var totalTime: String,
    var distance: String,
    var price: String,
    var location: ArrayList<Location>,
    var time : ArrayList<Time>,
    var vehiceId: String
) {
    constructor() : this("", "", "", "", "", "", "", ArrayList(), ArrayList(), "")

    constructor(idAdmin: String, departureLocation: String, destination: String, totalTime: String, distance: String, price: String, location: ArrayList<Location>, time: ArrayList<Time>, vehiceId: String)
            : this("", idAdmin, departureLocation, destination, totalTime, distance, price, location, time, vehiceId)
}
