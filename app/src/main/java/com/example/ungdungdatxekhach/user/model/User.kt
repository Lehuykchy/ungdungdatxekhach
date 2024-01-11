package com.example.ungdungdatxekhach.user.model

import com.example.ungdungdatxekhach.modelshare.Location
import org.mindrot.jbcrypt.BCrypt
import java.io.Serializable
import java.util.Date

data class User(
    var id: String? = null,
    var name: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var password: String? = null,
    var date: Date,
    var location: Location,
    var imageId: String,
) : Serializable {
    constructor() : this("", "", "", "", "", Date(), Location(),"")
    constructor(
        name: String,
        phone: String,
        email: String,
        password: String,
        date: Date,
        location: Location,
    ) : this("", name, phone, email, password, date, location,"")

    constructor(
        name: String,
        phone: String,
        email: String,
        password: String,
        date: Date,
        location: Location,
        imageId: String,
    ) : this("", name, phone, email, password, date, location,imageId)

    constructor(
        name: String,
        phone: String,
        email: String,
        date: Date,
        location: Location
    ) : this(null, name, phone, email, null, date, location,"")

    var hashedPassword: String? = password?.let { hashPassword(it) }
        private set

    companion object {
        private fun hashPassword(password: String): String {
            return BCrypt.hashpw(password, BCrypt.gensalt())
        }
    }
    fun hashPasswordChange(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    fun checkPassword(rawPassword: String): Boolean {
        return BCrypt.checkpw(rawPassword, hashedPassword)
    }
}