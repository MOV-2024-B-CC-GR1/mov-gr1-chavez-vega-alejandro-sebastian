package com.example.vaultix.ui.transaction

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vaultix.R
import com.example.vaultix.database.DatabaseHelper
import com.example.vaultix.model.Transaction

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class TransactionActivity : AppCompatActivity() {

    /**
     * Actividad para gestionar las transacciones de un cliente específico.
     * Permite visualizar, agregar, actualizar y eliminar transacciones.
     */
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerViewTransactions: RecyclerView
    private lateinit var adapter: TransactionsAdapter
    private val transactionsList = mutableListOf<Transaction>()
    private var clienteId: Int = 0

    /**
     * Método llamado al crear la actividad.
     * Configura el RecyclerView, el botón flotante y carga los datos del cliente.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)

        dbHelper = DatabaseHelper(this)
        recyclerViewTransactions = findViewById(R.id.recyclerViewTransactions)
        val fabAddTransaction: ExtendedFloatingActionButton = findViewById(R.id.fabAddTransaction)

        // Obtener el ID del cliente desde el Intent
        clienteId = intent.getIntExtra("clienteId", 0)

        setupRecyclerView()
        // Configurar el botón flotante para agregar una nueva transacción
        fabAddTransaction.setOnClickListener {
            val intent = Intent(this, TransactionFormActivity::class.java)
            intent.putExtra("clienteId", clienteId) // Pasar el ID del cliente al formulario
            startActivity(intent)
        }
    }

    /**
     * Configura el RecyclerView para mostrar la lista de transacciones.
     */
    private fun setupRecyclerView() {
        adapter = TransactionsAdapter(transactionsList) { transaction ->
            mostrarOpcionesTransaccion(transaction)
        }
        recyclerViewTransactions.layoutManager = LinearLayoutManager(this)
        recyclerViewTransactions.adapter = adapter
    }

    /**
     * Método llamado cuando la actividad vuelve a primer plano.
     * Carga las transacciones desde la base de datos y actualiza la lista.
     */
    override fun onResume() {
        super.onResume()
        cargarTransacciones()
    }

    /**
     * Carga las transacciones del cliente desde la base de datos y actualiza el RecyclerView.
     */
    private fun cargarTransacciones() {
        transactionsList.clear()
        val cursor = dbHelper.obtenerTransaccionesPorCliente(clienteId)
        if (cursor.moveToFirst()) {
            do {
                val transaction = Transaction(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACCION_ID)),
                    clienteId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACCION_CLIENTE_ID)),
                    monto = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACCION_MONTO)),
                    tipo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACCION_TIPO)),
                    fecha = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACCION_FECHA)),
                    categoria = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACCION_CATEGORIA)),
                    ubicacion = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACCION_UBICACION))
                )
                transactionsList.add(transaction)
            } while (cursor.moveToNext())
        }
        cursor.close()
        adapter.notifyDataSetChanged()
    }

    /**
     * Muestra un cuadro de diálogo con opciones para una transacción seleccionada.
     * @param transaction Transacción seleccionada.
     */
    private fun mostrarOpcionesTransaccion(transaction: Transaction) {
        val opciones = arrayOf("Actualizar", "Eliminar")
        AlertDialog.Builder(this)
            .setTitle("Opciones para la transacción")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> actualizarTransaccion(transaction)
                    1 -> eliminarTransaccion(transaction)
                }
            }
            .show()
    }

    /**
     * Inicia la actividad de formulario para actualizar una transacción.
     * @param transaction Transacción que se desea actualizar.
     */
    private fun actualizarTransaccion(transaction: Transaction) {
        val intent = Intent(this, TransactionFormActivity ::class.java)
        intent.putExtra("transactionId", transaction.id)
        intent.putExtra("clienteId", transaction.clienteId)
        startActivity(intent)
    }

    /**
     * Muestra un cuadro de diálogo para confirmar la eliminación de una transacción.
     * @param transaction Transacción que se desea eliminar.
     */
    private fun eliminarTransaccion(transaction: Transaction) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de eliminar esta transacción?")
            .setPositiveButton("Sí") { _, _ ->
                dbHelper.eliminarTransaccion(transaction.id)
                cargarTransacciones()
                Toast.makeText(this, "Transacción eliminada", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
