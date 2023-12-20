package com.example.ungdungdatxekhach.modelshare

import java.io.Serializable

data class Location(
    var city: String,
    var district: String,
    var ward: String,
    var other: String,
) : Serializable {
    constructor() : this("", "", "", "")
    constructor(
        city: String, district: String, ward: String
    ) : this(
        city,
        district,
        ward,
        "",

        )
}