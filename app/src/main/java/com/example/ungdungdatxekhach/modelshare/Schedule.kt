package com.example.ungdungdatxekhach.modelshare

import com.example.ungdungdatxekhach.admin.Constants
import com.example.ungdungdatxekhach.user.model.Ticket
import java.io.Serializable
import java.util.Date

data class Schedule(
    var id: String,
    var routeId: String,
    var customerIds: ArrayList<Ticket>,
    var departureLocation: String,
    var destinationLocation: String,
    var dateRoute: TimeRoute,
    var vehicleId: String,
    var date : Date,
    var status : String
) : Serializable {
    constructor() : this("", "", ArrayList(), "", "", TimeRoute(), "", Date(), "")
    constructor(
        routeId: String,
        dateRoute: TimeRoute,
        departureLocation: String,
        destinationLocation: String,
        vehicleId: String,
        date: Date,
    ) : this(
        "",  // You may want to generate a unique ID here
        routeId,
        ArrayList(),
        departureLocation,
        destinationLocation,
        dateRoute,
        vehicleId,
        date,
        Constants.STATUS_NO_START
    )
}
