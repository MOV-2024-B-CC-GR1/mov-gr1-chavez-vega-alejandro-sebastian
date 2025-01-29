package com.example.vaultix

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*
/**
 * Actividad para agregar o editar clientes en la base de datos.
 */
class ClientFormActivity : AppCompatActivity() {

    // Variables para manejar la base de datos y los elementos del diseño
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var etNombre: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etTelefono: TextInputEditText
    private lateinit var swActivo: SwitchMaterial
    private lateinit var btnGuardarCliente: Button

    private var clienteId: Int = 0
    private var isEditing = false // Indica si estamos editando un cliente existente

    /**
     * Método llamado al crear la actividad.
     * Configura los elementos de la interfaz y las validaciones.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_client)

        // Inicializar base de datos y elementos de la interfaz
        dbHelper = DatabaseHelper(this)
        etNombre = findViewById(R.id.etNombre)
        etEmail = findViewById(R.id.etEmail)
        etTelefono = findViewById(R.id.etTelefono)
        swActivo = findViewById(R.id.swActivo)
        btnGuardarCliente = findViewById(R.id.btnGuardarCliente)

        // Obtener el clienteId del Intent, si se está editando un cliente
        clienteId = intent.getIntExtra("clienteId", 0)
        if (clienteId != 0) {
            cargarCliente() // Cargar los datos del cliente para edición
            isEditing = true
        }

        setupValidations() // Configurar validaciones en tiempo real
        setupSaveButton()
    }

    /**
     * Configura las validaciones en tiempo real para los campos de email y teléfono.
     */
    private fun setupValidations() {
        etEmail.addTextChangedListener(createTextWatcher { validarEmail() })
        etTelefono.addTextChangedListener(createTextWatcher { validarTelefono() })
    }

    /**
     * Crea un TextWatcher genérico para aplicar validaciones en tiempo real.
     * @param validationFunction Función que se ejecutará después de cada cambio.
     */
    private fun createTextWatcher(validationFunction: () -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = validationFunction()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    /**
     * Configura el botón de guardar para agregar o actualizar un cliente.
     */
    private fun setupSaveButton() {
        btnGuardarCliente.setOnClickListener {
            if (validarCampos()) {
                if (isEditing) actualizarCliente() else agregarCliente()
            }
        }
    }

    /**
     * Valida que todos los campos sean correctos antes de guardar.
     * @return `true` si todos los campos son válidos, de lo contrario `false`.
     */
    private fun validarCampos(): Boolean {
        when {
            etNombre.text.isNullOrBlank() -> {
                showToast("El nombre es obligatorio")
                return false
            }
            etEmail.text.isNullOrBlank() -> {
                showToast("El correo es obligatorio")
                return false
            }
            etTelefono.text.isNullOrBlank() -> {
                showToast("El teléfono es obligatorio")
                return false
            }
            etEmail.error != null || etTelefono.error != null -> {
                showToast("Corrige los errores antes de continuar")
                return false
            }
        }
        return true
    }

    /**
     * Valida el formato del correo electrónico.
     */
    private fun validarEmail() {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        etEmail.error = if (!emailRegex.matches(etEmail.text.toString())) "Correo inválido" else null
    }

    /**
     * Valida el formato del teléfono.
     */
    private fun validarTelefono() {
        val telefonoRegex = Regex("^\\d{7,15}$")
        etTelefono.error = if (!telefonoRegex.matches(etTelefono.text.toString())) "Teléfono inválido (7-15 dígitos)" else null
    }

    /**
     * Agrega un nuevo cliente a la base de datos.
     */
    private fun agregarCliente() {
        val cliente = obtenerDatosCliente()
        dbHelper.insertarCliente(cliente.nombre, cliente.email, cliente.telefono, cliente.activo,
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
        showToast("Cliente agregado")
        finish()
    }

    /**
     * Actualiza los datos de un cliente existente en la base de datos.
     */
    private fun actualizarCliente() {
        val cliente = obtenerDatosCliente()
        dbHelper.actualizarCliente(clienteId, cliente.nombre, cliente.email, cliente.telefono, cliente.activo)
        showToast("Cliente actualizado")
        finish()
    }

    /**
     * Obtiene los datos del cliente ingresados en el formulario.
     * @return Objeto ClienteData con los datos ingresados.
     */
    private fun obtenerDatosCliente() = ClienteData(
        nombre = etNombre.text.toString(),
        email = etEmail.text.toString(),
        telefono = etTelefono.text.toString(),
        activo = swActivo.isChecked
    )

    /**
     * Carga los datos de un cliente existente en el formulario para edición.
     */
    private fun cargarCliente() {
        val cursor = dbHelper.obtenerClientes()
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.CLIENTE_ID)) == clienteId) {
                    etNombre.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.CLIENTE_NOMBRE)))
                    etEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.CLIENTE_EMAIL)))
                    etTelefono.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.CLIENTE_TELEFONO)))
                    swActivo.isChecked = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.CLIENTE_ACTIVO)) == 1
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
     * Clase interna para manejar los datos del cliente.
     */
    private data class ClienteData(
        val nombre: String,
        val email: String,
        val telefono: String,
        val activo: Boolean
    )
}