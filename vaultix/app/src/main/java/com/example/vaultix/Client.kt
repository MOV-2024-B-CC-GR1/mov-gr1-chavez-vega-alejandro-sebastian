package com.example.vaultix

data class Cliente(
    val id: Int = 0, // Identificador único del cliente
    var nombre: String, // Nombre del cliente
    var email: String, // Email del cliente
    var telefono: String, // Teléfono del cliente
    var activo: Boolean, // Estado del cliente (activo o inactivo)
    val fechaRegistro: String // Fecha de registro del cliente (en formato String para SQLite)
)