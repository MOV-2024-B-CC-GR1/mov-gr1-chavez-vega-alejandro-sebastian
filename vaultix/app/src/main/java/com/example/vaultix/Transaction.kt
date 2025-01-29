package com.example.vaultix

data class Pedido(
    val id: Int = 0, // Identificador único del pedido
    var cliente_id: Int, // ID del cliente al que pertenece el pedido
    var descripcion: String, // Descripción del pedido
    var estado: String, // Estado del pedido (por ejemplo: "Pendiente", "Completado")
    var fechaPedido: String // Fecha del pedido (en formato String para SQLite)
)