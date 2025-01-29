package com.example.vaultix.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.ccgr12024b_gasm.R

import com.example.vaultix.ui.clientes.ClientesActivity

/**
 * Actividad principal de la aplicación.
 * Muestra el menú principal y permite navegar a las secciones de la aplicación.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Método llamado al crear la actividad.
     * Configura la interfaz principal y la navegación al listado de clientes.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Asociar el layout activity_main.xml

        // Referencia al botón btnClientes
        val btnClientes: Button = findViewById(R.id.btnClientes)

        // Configurar la acción al hacer clic en el botón
        btnClientes.setOnClickListener {
            val intent = Intent(this, ClientesActivity::class.java)
            startActivity(intent)
        }
    }
}
