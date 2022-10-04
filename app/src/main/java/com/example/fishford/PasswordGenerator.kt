package com.example.fishford

class PasswordGenerator {

    private val chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    var passWord = ""

    fun generatePassword(): String {

        for (i in 0..8) {
            passWord += chars[Math.floor(Math.random() * chars.length).toInt()]
        }
        return passWord

    }



}