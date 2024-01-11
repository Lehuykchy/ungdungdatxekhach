package com.example.ungdungdatxekhach.modelshare.adapter

import com.example.ungdungdatxekhach.admin.Constants
import com.example.ungdungdatxekhach.admin.model.Admin
import com.example.ungdungdatxekhach.admin.model.Vehicle
import com.example.ungdungdatxekhach.modelshare.Route
import com.example.ungdungdatxekhach.modelshare.Schedule
import java.io.Serializable

data class Filter(var schedule: Schedule, var admin: Admin, var route: Route, var vehicle: Vehicle
) : Serializable {
    constructor() : this(Schedule(), Admin(), Route(), Vehicle())
//    constructor(
//        schedule: Schedule,
//        admin: Admin,
//        route: Route,
//        vehicle: Vehicle,
//    ) : this(
//        schedule,
//        admin,
//        route,
//        vehicle
//    )
}