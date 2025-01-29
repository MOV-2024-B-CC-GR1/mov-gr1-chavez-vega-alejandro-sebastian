package com.example.vaultix.ui.clientes

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.ccgr12024b_gasm.R
import com.example.vaultix.database.DatabaseHelper
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

/**
 * Actividad para agregar o editar clientes en la base de datos.
 */
class ClienteFormActivity : AppCompatActivity() {

    // Variables para manejar la base de datos y los elementos del diseño
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var etNombre: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etTelefono: TextInputEditText
    private lateinit var swActivo: SwitchMaterial
    private lateinit var swPremium: SwitchMaterial
    private lateinit var btnGuardarCliente: Button

    private var clienteId: Int = 0
    private var isEditing = false // Indica si estamos editando un cliente existente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cliente_form)

        // Inicializar base de datos y elementos de la interfaz
        dbHelper = DatabaseHelper(this)
        etNombre = findViewById(R.id.etNombre)
        etEmail = findViewById(R.id.etEmail)
        etTelefono = findViewById(R.id.etTelefono)
        swActivo = findViewById(R.id.swActivo)
        swPremium = findViewById(R.id.swPremium)
        btnGuardarCliente = findViewById(R.id.btnGuardarCliente)

        // Obtener el clienteId del Intent, si se está editando un cliente
        clienteId = intent.getIntExtra("clienteId", 0)
        if (clienteId != 0) {
            cargarCliente() // Cargar los datos del cliente para edición
            isEditing = true
        }

        setupValidations()
        setupSaveButton()
    }

    private fun setupValidations() {
        etEmail.addTextChangedListener(createTextWatcher { validarEmail() })
        etTelefono.addTextChangedListener(createTextWatcher { validarTelefono() })
    }

    private fun createTextWatcher(validationFunction: () -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = validationFunction()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    private fun setupSaveButton() {
        btnGuardarCliente.setOnClickListener {
            if (validarCampos()) {
                if (isEditing) actualizarCliente() else agregarCliente()
            }
        }
    }

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

    private fun validarEmail() {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        etEmail.error = if (!emailRegex.matches(etEmail.text.toString())) "Correo inválido" else null
    }

    private fun validarTelefono() {
        val telefonoRegex = Regex("^\\d{7,15}$")
        etTelefono.error = if (!telefonoRegex.matches(etTelefono.text.toString())) "Teléfono inválido (7-15 dígitos)" else null
    }

    private fun agregarCliente() {
        val cliente = obtenerDatosCliente()
        dbHelper.insertarCliente(
            cliente.nombre, cliente.email, cliente.telefono, cliente.activo,
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()), cliente.premium
        )
        showToast("Cliente agregado")
        finish()
    }

    private fun actualizarCliente() {
        val cliente = obtenerDatosCliente()
        dbHelper.actualizarCliente(clienteId, cliente.nombre, cliente.email, cliente.telefono, cliente.activo, cliente.premium)
        showToast("Cliente actualizado")
        finish()
    }

    private fun obtenerDatosCliente() = ClienteData(
        nombre = etNombre.text.toString(),
        email = etEmail.text.toString(),
        telefono = etTelefono.text.toString(),
        activo = swActivo.isChecked,
        premium = swPremium.isChecked
    )

    private fun cargarCliente() {
        val cursor = dbHelper.obtenerClientes()
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.CLIENTE_ID)) == clienteId) {
                    etNombre.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.CLIENTE_NOMBRE)))
                    etEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.CLIENTE_EMAIL)))
                    etTelefono.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.CLIENTE_TELEFONO)))
                    swActivo.isChecked = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.CLIENTE_ACTIVO)) == 1
                    swPremium.isChecked = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.CLIENTE_PREMIUM)) == 1
                    break
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private data class ClienteData(
        val nombre: String,
        val email: String,
        val telefono: String,
        val activo: Boolean,
        val premium: Boolean
    )
}
