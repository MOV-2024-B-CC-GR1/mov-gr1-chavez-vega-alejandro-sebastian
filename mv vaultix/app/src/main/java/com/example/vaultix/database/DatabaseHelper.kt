package com.example.vaultix.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DatabaseHelper es una clase para manejar la base de datos SQLite.
 * Contiene las operaciones CRUD para las tablas Cliente y Transaccion.
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "AppCRUD.db"
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

    override fun onCreate(db: SQLiteDatabase) {
        val createClienteTable = """
            CREATE TABLE $TABLE_CLIENTE (
                $CLIENTE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $CLIENTE_NOMBRE TEXT NOT NULL,
                $CLIENTE_EMAIL TEXT NOT NULL,
                $CLIENTE_TELEFONO TEXT NOT NULL,
                $CLIENTE_ACTIVO INTEGER NOT NULL,
                $CLIENTE_FECHA_REGISTRO TEXT NOT NULL,
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

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACCION")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CLIENTE")
        onCreate(db)
    }

    // CRUD para Cliente
    fun insertarCliente(nombre: String, email: String, telefono: String, activo: Boolean, fechaRegistro: String, premium: Boolean): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(CLIENTE_NOMBRE, nombre)
            put(CLIENTE_EMAIL, email)
            put(CLIENTE_TELEFONO, telefono)
            put(CLIENTE_ACTIVO, if (activo) 1 else 0)
            put(CLIENTE_FECHA_REGISTRO, fechaRegistro)
            put(CLIENTE_PREMIUM, if (premium) 1 else 0)
        }
        return db.insert(TABLE_CLIENTE, null, values)
    }

    fun obtenerClientes(): Cursor {
        val db = readableDatabase
        return db.query(TABLE_CLIENTE, null, null, null, null, null, "$CLIENTE_NOMBRE ASC")
    }

    fun actualizarCliente(id: Int, nombre: String, email: String, telefono: String, activo: Boolean, premium: Boolean): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(CLIENTE_NOMBRE, nombre)
            put(CLIENTE_EMAIL, email)
            put(CLIENTE_TELEFONO, telefono)
            put(CLIENTE_ACTIVO, if (activo) 1 else 0)
            put(CLIENTE_PREMIUM, if (premium) 1 else 0)
        }
        return db.update(TABLE_CLIENTE, values, "$CLIENTE_ID = ?", arrayOf(id.toString()))
    }

    fun eliminarCliente(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_CLIENTE, "$CLIENTE_ID = ?", arrayOf(id.toString()))
    }

    // CRUD para Transaccion
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

    fun eliminarTransaccion(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_TRANSACCION, "$TRANSACCION_ID = ?", arrayOf(id.toString()))
    }
}
