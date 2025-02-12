package com.example.vaultix.model

/**
 * Representa un cliente en el sistema.
 *
 * @property id Identificador único del cliente (autogenerado en la base de datos).
 * @property nombre Nombre completo del cliente.
 * @property email Correo electrónico del cliente.
 * @property telefono Número de teléfono del cliente.
 * @property activo Indica si el cliente está activo (true) o inactivo (false) en el sistema.
 * @property fechaRegistro Fecha en la que se registró el cliente, almacenada como String (compatible con SQLite).
 */

data class Cliente(
    val id: Int = 0, // Identificador único del cliente
    var nombre: String, // Nombre del cliente
    var email: String, // Email del cliente
    var telefono: String, // Teléfono del cliente
    var activo: Boolean, // Estado del cliente (activo o inactivo)
    val fechaRegistro: String, // Fecha de registro del cliente (en formato String para SQLite)
    var latitud: Double? = null,
    var longitud: Double? = null,
    var premium: Boolean // Indica si el cliente es premium o no

)
