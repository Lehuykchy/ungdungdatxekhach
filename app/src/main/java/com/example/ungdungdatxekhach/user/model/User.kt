package com.example.ungdungdatxekhach.user.model

import org.mindrot.jbcrypt.BCrypt

data class User(
    var id: String? = null,
    var name: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var password: String ? = null,
) {
    constructor() : this("", "", "", "")
    constructor(name: String, phone: String, email: String, password: String) : this("", name, phone, email, password)

    var hashedPassword: String? = password?.let { hashPassword(it) }
        private set

    companion object {
        private fun hashPassword(password: String): String {
            return BCrypt.hashpw(password, BCrypt.gensalt())
        }
    }fun checkPassword(rawPassword: String): Boolean {
        return BCrypt.checkpw(rawPassword, hashedPassword)
    }
}