package com.example.ungdungdatxekhach.user.model

import com.example.ungdungdatxekhach.modelshare.TimeRoute
import java.io.Serializable
import java.util.Date

data class Ticket(
    var id: String,
    var customerId: String,
    var adminId : String,
    var scheduleId: String,
    var routeId:String,
    var departure: String,
    var destination: String,
    var phone: String,
    var email: String,
    var name: String,
    var description: String,
    var count: Int,
    var statusPay: Boolean,
    var createAt: Date,
    var totalPrice: String,

    ) : Serializable {
    constructor() : this("", "", "","","", "", "", "", "", "","", 0, false, Date(), "")
    constructor(
        customerId: String,
        scheduleId: String,
        routeId: String,
        departure: String,
        destination: String,
        count: Int,
        date: Date,
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
        false,
        date,
        totalPrice
    )
}