package com.example.vaultix.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * DatabaseHelper es una clase para manejar la base de datos SQLite.
 * Contiene las operaciones CRUD para las tablas Cliente y Pedido.
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = " vaultix.db"
        private const val DATABASE_VERSION = 1

        // Tablas
        const val TABLE_CLIENTE = "Cliente"
        const val TABLE_TRANSACCION = "Transaccion"

        // Campos de Cliente
        const val CLIENTE_ID = "id"
        const val CLIENTE_NOMBRE = "nombre"
        const val CLIENTE_EMAIL = "email"
        const val CLIENTE_TELEFONO = "telefono"
        const val CLIENTE_ACTIVO = "activo"
        const val CLIENTE_FECHA_REGISTRO = "fechaRegistro"
        const val CLIENTE_LATITUD = "latitud"
        const val CLIENTE_LONGITUD = "longitud"
        const val CLIENTE_PREMIUM = "premium"

        // Campos de Transaccion
        const val TRANSACCION_ID = "id"
        const val TRANSACCION_CLIENTE_ID = "cliente_id"
        const val TRANSACCION_MONTO = "monto"
        const val TRANSACCION_TIPO = "tipo"
        const val TRANSACCION_FECHA = "fecha"
        const val TRANSACCION_CATEGORIA = "categoria"
        const val TRANSACCION_UBICACION = "ubicacion"
    }

    /**
     * Se ejecuta al crear la base de datos por primera vez.
     * Aquí se crean las tablas Cliente y Pedido.
     */
    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla Cliente
        val createClienteTable = """
            CREATE TABLE $TABLE_CLIENTE (
                $CLIENTE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $CLIENTE_NOMBRE TEXT NOT NULL,
                $CLIENTE_EMAIL TEXT NOT NULL,
                $CLIENTE_TELEFONO TEXT NOT NULL,
                $CLIENTE_ACTIVO INTEGER NOT NULL,
                $CLIENTE_FECHA_REGISTRO TEXT NOT NULL,
                $CLIENTE_LATITUD REAL,
                $CLIENTE_LONGITUD REAL,
                $CLIENTE_PREMIUM INTEGER NOT NULL
            );
        """
        db.execSQL(createClienteTable)


        val createTransaccionTable = """
            CREATE TABLE $TABLE_TRANSACCION (
                $TRANSACCION_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $TRANSACCION_CLIENTE_ID INTEGER NOT NULL,
                $TRANSACCION_MONTO REAL NOT NULL,
                $TRANSACCION_TIPO TEXT NOT NULL,
                $TRANSACCION_FECHA TEXT NOT NULL,
                $TRANSACCION_CATEGORIA TEXT NOT NULL,
                $TRANSACCION_UBICACION TEXT NOT NULL,
                FOREIGN KEY ($TRANSACCION_CLIENTE_ID) REFERENCES $TABLE_CLIENTE($CLIENTE_ID)
            );
        """
        db.execSQL(createTransaccionTable)
    }

    /**
     * Se ejecuta cuando la base de datos necesita ser actualizada.
     * Elimina las tablas existentes y las recrea.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Si estamos actualizando desde la versión 1 a la 2 (agregando columnas de latitud y longitud)
        if (oldVersion == 1 && newVersion == 2) {
            try {
                // Intentar agregar las nuevas columnas a la tabla existente
                db.execSQL("ALTER TABLE $TABLE_CLIENTE ADD COLUMN $CLIENTE_LATITUD REAL;")
                db.execSQL("ALTER TABLE $TABLE_CLIENTE ADD COLUMN $CLIENTE_LONGITUD REAL;")
            } catch (e: Exception) {
                // Si algo sale mal, eliminar las tablas y recrearlas
                db.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACCION")
                db.execSQL("DROP TABLE IF EXISTS $TABLE_CLIENTE")
                onCreate(db)
            }
        } else {
            // Para otros casos, mantener el comportamiento original
            db.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACCION")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_CLIENTE")
            onCreate(db)
        }
    }


    // --- CRUD para la tabla Cliente ---

    /**
     * Inserta un nuevo cliente en la tabla Cliente.
     * @param nombre Nombre del cliente.
     * @param email Correo electrónico del cliente.
     * @param telefono Teléfono del cliente.
     * @param activo Indica si el cliente está activo (true/false).
     * @param fechaRegistro Fecha de registro del cliente.
     * @return ID del cliente recién insertado.
     */
    fun insertarCliente(nombre: String, email: String, telefono: String, activo: Boolean,
                        fechaRegistro: String, latitud: Double?, longitud: Double?,premium: Boolean): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(CLIENTE_NOMBRE, nombre)
            put(CLIENTE_EMAIL, email)
            put(CLIENTE_TELEFONO, telefono)
            put(CLIENTE_ACTIVO, if (activo) 1 else 0)
            put(CLIENTE_FECHA_REGISTRO, fechaRegistro)
            put(CLIENTE_LATITUD, latitud)
            put(CLIENTE_LONGITUD, longitud)
            put(CLIENTE_PREMIUM, if (premium) 1 else 0)
        }
        return db.insert(TABLE_CLIENTE, null, values)
    }

    /**
     * Obtiene todos los clientes de la tabla Cliente.
     * @return Cursor con los resultados de la consulta.
     */
    fun obtenerClientes(): Cursor {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_CLIENTE, null, null, null, null, null, "$CLIENTE_NOMBRE ASC"
        )

        // Log para verificar los datos
        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(CLIENTE_NOMBRE))
                val latIndex = cursor.getColumnIndexOrThrow(CLIENTE_LATITUD)
                val longIndex = cursor.getColumnIndexOrThrow(CLIENTE_LONGITUD)
                val lat = if (!cursor.isNull(latIndex)) cursor.getDouble(latIndex) else null
                val long = if (!cursor.isNull(longIndex)) cursor.getDouble(longIndex) else null

                android.util.Log.d("DatabaseHelper", "Cliente en DB: $nombre - Lat: $lat, Long: $long")
            } while (cursor.moveToNext())
            cursor.moveToFirst() // Regresamos el cursor al inicio
        }

        return cursor
    }

    /**
     * Actualiza los datos de un cliente existente.
     * @param id ID del cliente a actualizar.
     * @param nombre Nuevo nombre del cliente.
     * @param email Nuevo correo electrónico del cliente.
     * @param telefono Nuevo teléfono del cliente.
     * @param activo Indica si el cliente está activo.
     * @return Número de filas afectadas.
     */
    fun actualizarCliente(id: Int, nombre: String, email: String, telefono: String,
                          activo: Boolean, latitud: Double?, longitud: Double?, premium: Boolean): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(CLIENTE_NOMBRE, nombre)
            put(CLIENTE_EMAIL, email)
            put(CLIENTE_TELEFONO, telefono)
            put(CLIENTE_ACTIVO, if (activo) 1 else 0)
            put(CLIENTE_LATITUD, latitud)
            put(CLIENTE_LONGITUD, longitud)
            put(CLIENTE_PREMIUM, if (premium) 1 else 0)
        }
        return db.update(TABLE_CLIENTE, values, "$CLIENTE_ID = ?", arrayOf(id.toString()))
    }

    /**
     * Elimina un cliente de la tabla Cliente.
     * @param id ID del cliente a eliminar.
     * @return Número de filas afectadas.
     */
    fun eliminarCliente(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_CLIENTE, "$CLIENTE_ID = ?", arrayOf(id.toString()))
    }

    // --- CRUD para la tabla Pedido ---

    /**
     * Inserta un nuevo pedido en la tabla Pedido.
     * @param clienteId ID del cliente asociado al pedido.
     * @param descripcion Descripción del pedido.
     * @param estado Estado del pedido.
     * @param fechaPedido Fecha del pedido.
     * @return ID del pedido recién insertado.
     */
    fun insertarTransaccion(clienteId: Int, monto: Double, tipo: String, fecha: String, categoria: String, ubicacion: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(TRANSACCION_CLIENTE_ID, clienteId)
            put(TRANSACCION_MONTO, monto)
            put(TRANSACCION_TIPO, tipo)
            put(TRANSACCION_FECHA, fecha)
            put(TRANSACCION_CATEGORIA, categoria)
            put(TRANSACCION_UBICACION, ubicacion)
        }
        return db.insert(TABLE_TRANSACCION, null, values)
    }

    /**
     * Obtiene todos los pedidos asociados a un cliente.
     * @param clienteId ID del cliente.
     * @return Cursor con los resultados de la consulta.
     */
    fun obtenerTransaccionesPorCliente(clienteId: Int): Cursor {
        val db = readableDatabase
        return db.query(
            TABLE_TRANSACCION,
            null,
            "$TRANSACCION_CLIENTE_ID = ?",
            arrayOf(clienteId.toString()),
            null,
            null,
            "$TRANSACCION_FECHA DESC"
        )
    }

    /**
     * Actualiza los datos de un pedido existente.
     * @param id ID del pedido a actualizar.
     * @param descripcion Nueva descripción del pedido.
     * @param estado Nuevo estado del pedido.
     * @return Número de filas afectadas.
     */
    fun actualizarTransaccion(id: Int, monto: Double, tipo: String, categoria: String, ubicacion: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(TRANSACCION_MONTO, monto)
            put(TRANSACCION_TIPO, tipo)
            put(TRANSACCION_CATEGORIA, categoria)
            put(TRANSACCION_UBICACION, ubicacion)
        }
        return db.update(TABLE_TRANSACCION, values, "$TRANSACCION_ID = ?", arrayOf(id.toString()))
    }

    /**
     * Elimina un pedido de la tabla Pedido.
     * @param id ID del pedido a eliminar.
     * @return Número de filas afectadas.
     */
    fun eliminarTransaccion(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_TRANSACCION, "$TRANSACCION_ID = ?", arrayOf(id.toString()))
    }
}
