package com.example.ungdungdatxekhach.modelshare

import java.io.Serializable
import java.util.Date

data class Schedule(
    var id: String,
    var routeId: String,
    var customerIds: ArrayList<String>,
    var dateRoute: TimeRoute,
    var vehicleId: String,
    var date : Date,
) : Serializable {
    constructor() : this("", "", ArrayList(), TimeRoute(), "", Date())
    constructor(
        routeId: String,
        dateRoute: TimeRoute,
        vehicleId: String,
        date: Date,
    ) : this(
        "",  // You may want to generate a unique ID here
        routeId,
        ArrayList(),
        dateRoute,
        vehicleId,
        date
    )
}
