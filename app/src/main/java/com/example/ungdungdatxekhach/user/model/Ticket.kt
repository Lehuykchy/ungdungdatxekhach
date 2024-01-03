package com.example.ungdungdatxekhach.user.model

import com.example.ungdungdatxekhach.modelshare.Location
import com.example.ungdungdatxekhach.modelshare.TimeRoute
import java.io.Serializable
import java.util.Date

data class Ticket(
    var id: String,
    var customerId: String,
    var adminId : String,
    var scheduleId: String,
    var routeId:String,
    var departure: Location,
    var destination: Location,
    var phone: String,
    var email: String,
    var name: String,
    var description: String,
    var count: Int,
    var status: String,
    var createAt: Date,
    var totalPrice: String,
    var timeRoute: TimeRoute,
    var dateDeparture: Date
    ) : Serializable {
    constructor() : this("", "", "","","", Location(), Location(), "", "", "","", 0, "", Date(), "", TimeRoute(), Date())
    constructor(
        customerId: String,
        scheduleId: String,
        routeId: String,
        departure: Location,
        destination: Location,
        count: Int,
        createAt: Date,
        totalPrice: String
    ) : this(
        "",  // You may want to generate a unique ID here
        customerId,
        "",
        scheduleId,
        routeId,
        departure,
        destination,
        "",
        "",
        "",
        "",
        count,
        "",
        createAt,
        totalPrice,
        TimeRoute(),
        Date(),
    )

    constructor(
        customerId: String,
        departure: Location,
        destination: Location,
        phone: String,
        email: String,
        name: String,
        count: Int,
        status: String,
        totalPrice: String,
        timeRoute: TimeRoute,
        dateDeparture: Date,
    ) : this(
        "",  // You may want to generate a unique ID here
        customerId,
        "",
        "",
        "",
        departure,
        destination,
        phone,
        email,
        name,
        "",
        count,
        status,
        Date(),
        totalPrice,
        timeRoute,
        dateDeparture,
    )
}