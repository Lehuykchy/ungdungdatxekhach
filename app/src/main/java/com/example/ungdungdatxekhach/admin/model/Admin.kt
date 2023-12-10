package com.example.ungdungdatxekhach.admin.model

import org.mindrot.jbcrypt.BCrypt

data class Admin(
    var id: String? = null,
    var name: String? = null,
    var phone: String? = null,
    var email: String? = null,
) {
    constructor() : this("", "", "")
    constructor(name: String, phone: String, email: String) : this("", name, phone, email)
}