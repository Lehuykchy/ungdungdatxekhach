package com.example.ungdungdatxekhach.modelshare

import java.io.Serializable

data class Route(
    var id: String,
    var idAdmin: String,
    var departureLocation: String,
    var destination: String,
    var totalTime: Int,
    var distance: String,
    var price: String,
    var location: ArrayList<Location>,
) : Serializable {
    constructor() : this("", "", "", "", 0, "", "", ArrayList())

    constructor(
        idAdmin: String,
        departureLocation: String,
        destination: String,
        totalTime: Int,
        distance: String,
        price: String,
        location: ArrayList<Location>,
    )
            : this(
        "",
        idAdmin,
        departureLocation,
        destination,
        totalTime,
        distance,
        price,
        location,
    )
}
