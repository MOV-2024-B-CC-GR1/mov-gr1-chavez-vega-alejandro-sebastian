package com.example.vaultix.ui.pedidos

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.ccgr12024b_gasm.R
import com.example.vaultix.database.DatabaseHelper
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.button.MaterialButton

/**
 * Actividad para agregar o editar transacciones en la base de datos.
 */
class PedidoFormActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var etMonto: TextInputEditText
    private lateinit var etTipo: MaterialAutoCompleteTextView
    private lateinit var etCategoria: TextInputEditText
    private lateinit var etUbicacion: TextInputEditText
    private lateinit var btnGuardarTransaccion: MaterialButton

    private var transactionId: Int = 0
    private var clienteId: Int = 0
    private var isEditing = false

    /**
     * Método llamado al crear la actividad.
     * Configura los elementos de la interfaz y carga los datos.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedido_form)

        setupViews()
        loadData()
        setupSaveButton()
    }

    /**
     * Configura los elementos de la interfaz, incluyendo el dropdown de tipo de transacción.
     */
    private fun setupViews() {
        dbHelper = DatabaseHelper(this)
        etMonto = findViewById(R.id.etMonto)
        etTipo = findViewById(R.id.etTipo)
        etCategoria = findViewById(R.id.etCategoria)
        etUbicacion = findViewById(R.id.etUbicacion)
        btnGuardarTransaccion = findViewById(R.id.btnGuardarTransaccion)

        // Tipos de transacciones disponibles
        val tipos = arrayOf("Ingreso", "Gasto")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tipos)
        etTipo.setAdapter(adapter)

        // Hacer el campo clickeable y mostrar dropdown al hacer click
        etTipo.setOnClickListener {
            etTipo.showDropDown()
        }
    }

    /**
     * Carga los datos necesarios de los extras del Intent.
     * Si se está editando una transacción, carga sus datos.
     */
    private fun loadData() {
        clienteId = intent.getIntExtra("clienteId", 0)
        transactionId = intent.getIntExtra("transactionId", 0)

        if (transactionId != 0) {
            cargarTransaccion()
            isEditing = true
        }
    }

    /**
     * Configura el botón de guardar para agregar o actualizar una transacción.
     */
    private fun setupSaveButton() {
        btnGuardarTransaccion.setOnClickListener {
            if (validarCampos()) {
                if (isEditing) actualizarTransaccion() else agregarTransaccion()
            }
        }
    }

    /**
     * Valida que todos los campos sean correctos antes de guardar.
     * @return `true` si todos los campos son válidos, de lo contrario `false`.
     */
    private fun validarCampos(): Boolean {
        if (etMonto.text.isNullOrBlank() || etTipo.text.isNullOrBlank() ||
            etCategoria.text.isNullOrBlank() || etUbicacion.text.isNullOrBlank()) {
            showToast("Todos los campos son obligatorios")
            return false
        }
        return true
    }

    /**
     * Agrega una nueva transacción a la base de datos.
     */
    private fun agregarTransaccion() {
        val transaccion = obtenerDatosTransaccion()
        dbHelper.insertarTransaccion(clienteId, transaccion.monto, transaccion.tipo,
            transaccion.fecha, transaccion.categoria, transaccion.ubicacion)
        showToast("Transacción agregada")
        finish()
    }

    /**
     * Actualiza los datos de una transacción existente en la base de datos.
     */
    private fun actualizarTransaccion() {
        val transaccion = obtenerDatosTransaccion()
        dbHelper.actualizarTransaccion(transactionId, transaccion.monto, transaccion.tipo,
            transaccion.categoria, transaccion.ubicacion)
        showToast("Transacción actualizada")
        finish()
    }

    /**
     * Obtiene los datos de la transacción ingresados en el formulario.
     * @return Objeto Transaction con los datos ingresados.
     */
    private fun obtenerDatosTransaccion() = Transaction(
        clienteId = clienteId,
        monto = etMonto.text.toString().toDouble(),
        tipo = etTipo.text.toString(),
        fecha = System.currentTimeMillis().toString(),
        categoria = etCategoria.text.toString(),
        ubicacion = etUbicacion.text.toString()
    )

    /**
     * Carga los datos de una transacción existente en el formulario para edición.
     */
    private fun cargarTransaccion() {
        val cursor = dbHelper.obtenerTransaccionesPorCliente(clienteId)
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACCION_ID)) == transactionId) {
                    etMonto.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACCION_MONTO)))
                    val tipoActual = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACCION_TIPO))
                    etTipo.setText(tipoActual, false)  // false para no filtrar el dropdown
                    etCategoria.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACCION_CATEGORIA)))
                    etUbicacion.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACCION_UBICACION)))
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
     * Clase interna para manejar los datos de la transacción.
     * @property monto Monto de la transacción.
     * @property tipo Tipo de la transacción (Ingreso/Gasto).
     * @property categoria Categoría de la transacción.
     * @property ubicacion Ubicación de la transacción.
     */
    private data class Transaction(
        val id: Int = 0,
        var clienteId: Int,
        var monto: Double,
        var tipo: String,
        var fecha: String,
        var categoria: String,
        var ubicacion: String
    )
}
