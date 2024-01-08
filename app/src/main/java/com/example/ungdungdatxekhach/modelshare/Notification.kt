package com.example.ungdungdatxekhach.modelshare

import com.example.ungdungdatxekhach.admin.Constants
import com.example.ungdungdatxekhach.user.model.Ticket
import java.io.Serializable
import java.util.Date

data class Notification(
    var id: String,
    var ticketId: String,
    var infomation: String,
    var date: Date,
) : Serializable {
    constructor() : this("", "","", Date())
    constructor(
        ticketId: String,
        infomation: String,
        date: Date,
    ) : this(
        "",  // You may want to generate a unique ID here
        ticketId,
        infomation,
        date,
    )
}
