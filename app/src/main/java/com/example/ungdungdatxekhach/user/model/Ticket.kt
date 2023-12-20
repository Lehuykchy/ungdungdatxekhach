package com.example.ungdungdatxekhach.user.model

import com.example.ungdungdatxekhach.modelshare.TimeRoute
import java.io.Serializable
import java.util.Date

data class Ticket(
    var id: String,
    var customerId: String,
    var scheduleId: String,
    var departure: String,
    var destination: String,
    var count: Int,
    var statusPay: Boolean,
    var createAt: Date,

) : Serializable {
    constructor() : this("", "", "", "", "", 0, false,Date())
    constructor(
        customerId: String,
        scheduleId: String,
        departure: String,
        destination: String,
        count: Int,
        date: Date,
    ) : this(
        "",  // You may want to generate a unique ID here
        customerId,
        scheduleId,
        departure,
        destination,
        count,
        false,
        date
    )
}