package com.example.ungdungdatxekhach.modelshare

import java.io.Serializable
import java.util.Date

data class Evaluate(
    var id: String,
    var customerId: String,
    var adminId: String,
    var ticketId: String,
    var scheduleId: String,
    var evaluate: Int,
    var comment : String,
    var date : Date,
    var nameCustomer: String
) : Serializable {
    constructor() : this("", "", "", "", "", 0, "", Date(),"")
    constructor(
        customerId: String,
        adminId: String,
        ticketId: String,
        scheduleId: String,
        evaluate: Int,
        comment: String,
        date : Date,
        nameCustomer: String
    ) : this (
        "",
        customerId,
        adminId,
        ticketId,
        scheduleId,
        evaluate,
        comment,
        date,
        nameCustomer
    )
}