package com.example.ungdungdatxekhach.admin.model


import com.example.ungdungdatxekhach.modelshare.Evaluate
import com.example.ungdungdatxekhach.modelshare.Location
import org.mindrot.jbcrypt.BCrypt
import java.io.Serializable

data class Admin(
    var id: String,
    var name: String,
    var phone: String,
    var email: String,
    var location: Location,
    var description: String,
    var imageBackGroundId: String,
    var evaluate: Double

) : Serializable {
    constructor() : this("","", "", "", Location(), "", "", 5.0)
    constructor(name: String, phone: String, email: String) : this("", name, phone, email, Location(), "", "", 5.0)
    constructor(name: String, phone: String, email: String, location: Location, description: String) : this("", name, phone, email, location, description, "", 5.0)
}