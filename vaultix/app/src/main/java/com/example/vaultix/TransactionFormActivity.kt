package com.example.vaultix

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.button.MaterialButton

/**
 * Actividad para agregar o editar pedidos en la base de datos.
 */
class TransactionFormActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var etDescripcion: TextInputEditText
    private lateinit var etEstado: MaterialAutoCompleteTextView
    private lateinit var btnGuardarPedido: MaterialButton

    private var pedidoId: Int = 0
    private var clienteId: Int = 0
    private var isEditing = false

    /**
     * Método llamado al crear la actividad.
     * Configura los elementos de la interfaz y carga los datos.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transaction_form)

        setupViews()
        loadData()
        setupSaveButton()
    }

    /**
     * Configura los elementos de la interfaz, incluyendo el dropdown de estados.
     */
    private fun setupViews() {
        dbHelper = DatabaseHelper(this)
        etDescripcion = findViewById(R.id.etNombre)
        btnGuardarPedido = findViewById(R.id.btnGuardar)

        // Estados disponibles
        val estados = arrayOf("Pendiente", "En proceso", "Completado", "Cancelado")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, estados)
        etEstado.setAdapter(adapter)

        // Hacer el campo clickeable y mostrar dropdown al hacer click
        etEstado.setOnClickListener {
            etEstado.showDropDown()
        }
    }

    /**
     * Carga los datos necesarios de los extras del Intent.
     * Si se está editando un pedido, carga sus datos.
     */
    private fun loadData() {
        clienteId = intent.getIntExtra("clienteId", 0)
        pedidoId = intent.getIntExtra("pedidoId", 0)

        if (pedidoId != 0) {
            cargarPedido()
            isEditing = true
        }
    }

    /**
     * Configura el botón de guardar para agregar o actualizar un pedido.
     */
    private fun setupSaveButton() {
        btnGuardarPedido.setOnClickListener {
            if (validarCampos()) {
                if (isEditing) actualizarPedido() else agregarPedido()
            }
        }
    }

    /**
     * Valida que todos los campos sean correctos antes de guardar.
     * @return `true` si todos los campos son válidos, de lo contrario `false`.
     */
    private fun validarCampos(): Boolean {
        if (etDescripcion.text.isNullOrBlank() || etEstado.text.isNullOrBlank()) {
            showToast("Todos los campos son obligatorios")
            return false
        }
        return true
    }

    /**
     * Agrega un nuevo pedido a la base de datos.
     */
    private fun agregarPedido() {
        val pedido = obtenerDatosPedido()
        dbHelper.insertarPedido(clienteId, pedido.descripcion, pedido.estado,
            System.currentTimeMillis().toString())
        showToast("Pedido agregado")
        finish()
    }

    /**
     * Actualiza los datos de un pedido existente en la base de datos.
     */
    private fun actualizarPedido() {
        val pedido = obtenerDatosPedido()
        dbHelper.actualizarPedido(pedidoId, pedido.descripcion, pedido.estado)
        showToast("Pedido actualizado")
        finish()
    }

    /**
     * Obtiene los datos del pedido ingresados en el formulario.
     * @return Objeto PedidoData con los datos ingresados.
     */
    private fun obtenerDatosPedido() = PedidoData(
        descripcion = etDescripcion.text.toString(),
        estado = etEstado.text.toString()
    )

    /**
     * Carga los datos de un pedido existente en el formulario para edición.
     */
    private fun cargarPedido() {
        val cursor = dbHelper.obtenerPedidosPorCliente(clienteId)
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.PEDIDO_ID)) == pedidoId) {
                    etDescripcion.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PEDIDO_DESCRIPCION)))
                    val estadoActual = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PEDIDO_ESTADO))
                    etEstado.setText(estadoActual, false)  // false para no filtrar el dropdown
                    break
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    /**
     * Muestra un mensaje Toast al usuario.
     * @param message Mensaje a mostrar.
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Clase interna para manejar los datos del pedido.
     * @property descripcion Descripción del pedido.
     * @property estado Estado actual del pedido.
     */
    private data class PedidoData(
        val descripcion: String,
        val estado: String
    )
}