package com.example.ungdungdatxekhach.admin.model

import java.io.Serializable

data class Vehicle(
    var id: String,
    var idAdmin: String,
    var type: String,
    var licensePlate: String,
    var seats: Int
) : Serializable {
    constructor() : this("", "", "", "", 0)

    constructor(
        idAdmin: String,
        type: String,
        licensePlate: String,
        seats: Int
    ) : this (
        "",
        idAdmin,
        type,
        licensePlate,
        seats
    )
}
