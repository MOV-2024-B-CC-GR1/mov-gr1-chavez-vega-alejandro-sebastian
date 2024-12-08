package com.example.examen

data class Transaction(
    val id: String,
    var date: String,
    var amount: Double,
    var paymentMethod: String,
    val frequency: String,
    val userId: String
)
