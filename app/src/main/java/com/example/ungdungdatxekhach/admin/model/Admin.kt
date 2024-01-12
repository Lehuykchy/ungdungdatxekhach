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

) : Serializable {
    constructor() : this("","", "", "", Location(), "", "")
    constructor(name: String, phone: String, email: String) : this("", name, phone, email, Location(), "", "")
    constructor(name: String, phone: String, email: String, location: Location, description: String, imageBackGroundId: String) : this("", name, phone, email, location, description, imageBackGroundId)
}