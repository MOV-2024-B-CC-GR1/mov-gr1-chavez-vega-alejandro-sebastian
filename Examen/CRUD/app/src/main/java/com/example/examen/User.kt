package com.example.examen

data class User(
    val id: String,
    var email: String,
    var birthday: String,
    var country: String,
    var preferences: String,
    var income: Double,
    var password: String,
    var isPremium: String?
)
