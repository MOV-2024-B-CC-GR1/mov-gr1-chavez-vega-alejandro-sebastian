package com.example.vaultix.model

/**
 * Representa una transacción realizada por un cliente en el sistema.
 *
 * @property id Identificador único de la transacción (autogenerado en la base de datos).
 * @property clienteId ID del cliente al que está asociada esta transacción.
 * @property monto Monto de la transacción.
 * @property tipo Tipo de transacción (por ejemplo: "Ingreso", "Gasto").
 * @property fecha Fecha en la que se realizó la transacción.
 * @property categoria Categoría de la transacción (por ejemplo: "Alimentos", "Transporte").
 * @property ubicacion Ubicación donde se realizó la transacción.
 */
data class Transaction(
    val id: Int = 0, // Identificador único de la transacción
    var clienteId: Int, // ID del cliente al que pertenece la transacción
    var monto: Double, // Monto de la transacción
    var tipo: String, // Tipo de transacción (Ingreso/Gasto)
    var fecha: String, // Fecha de la transacción (en formato String para SQLite)
    var categoria: String, // Categoría de la transacción
    var ubicacion: String // Ubicación de la transacción
)
