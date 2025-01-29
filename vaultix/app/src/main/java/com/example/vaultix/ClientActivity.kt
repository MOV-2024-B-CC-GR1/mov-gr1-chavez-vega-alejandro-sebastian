package com.example.vaultix
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class ClientActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerViewClientes: RecyclerView
    private lateinit var adapter: ClientesAdapter
    private val clientesList = mutableListOf<Cliente>()

    /**
     * Método llamado al crear la actividad.
     * Inicializa los componentes y configura la interacción con el botón flotante y el RecyclerView.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clientes)

        dbHelper = DatabaseHelper(this) // Inicializar la base de datos
        recyclerViewClientes = findViewById(R.id.recyclerViewClientes)
        val fabAddCliente: ExtendedFloatingActionButton = findViewById(R.id.fabAddCliente)

        setupRecyclerView() // Configurar el RecyclerView

        // Configurar botón flotante para agregar un cliente
        fabAddCliente.setOnClickListener {
            val intent = Intent(this, ClienteFormActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Configura el RecyclerView para mostrar la lista de clientes y manejar eventos de clic.
     */
    private fun setupRecyclerView() {
        adapter = ClientesAdapter(clientesList) { cliente ->
            mostrarOpcionesCliente(cliente)
        }
        recyclerViewClientes.layoutManager = LinearLayoutManager(this)
        recyclerViewClientes.adapter = adapter
    }

    /**
     * Método llamado cuando la actividad vuelve a primer plano.
     * Carga los clientes desde la base de datos para actualizar la lista.
     */
    override fun onResume() {
        super.onResume()
        cargarClientes()
    }

    /**
     * Carga los clientes desde la base de datos y actualiza el RecyclerView.
     */
    private fun cargarClientes() {
        clientesList.clear()
        val cursor = dbHelper.obtenerClientes()
        if (cursor.moveToFirst()) {
            do {
                val cliente = Cliente(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.CLIENTE_ID)),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.CLIENTE_NOMBRE)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.CLIENTE_EMAIL)),
                    telefono = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.CLIENTE_TELEFONO)),
                    activo = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.CLIENTE_ACTIVO)) == 1,
                    fechaRegistro = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.CLIENTE_FECHA_REGISTRO))
                )
                clientesList.add(cliente)
            } while (cursor.moveToNext())
        }
        cursor.close()
        adapter.notifyDataSetChanged()
    }

    /**
     * Muestra un cuadro de diálogo con opciones para un cliente seleccionado.
     * @param cliente Cliente seleccionado.
     */
    private fun mostrarOpcionesCliente(cliente: Cliente) {
        val opciones = arrayOf("Actualizar", "Eliminar", "Ver Pedidos")
        AlertDialog.Builder(this)
            .setTitle("Opciones para ${cliente.nombre}")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> actualizarCliente(cliente)
                    1 -> eliminarCliente(cliente)
                    2 -> verPedidos(cliente)
                }
            }
            .show()
    }

    /**
     * Inicia la actividad de formulario para actualizar un cliente.
     * @param cliente Cliente a actualizar.
     */
    private fun actualizarCliente(cliente: Cliente) {
        val intent = Intent(this, ClienteFormActivity::class.java)
        intent.putExtra("clienteId", cliente.id)
        startActivity(intent)
    }

    /**
     * Muestra un cuadro de diálogo para confirmar la eliminación de un cliente.
     * @param cliente Cliente a eliminar.
     */
    private fun eliminarCliente(cliente: Cliente) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de eliminar a ${cliente.nombre}?")
            .setPositiveButton("Sí") { _, _ ->
                dbHelper.eliminarCliente(cliente.id)
                cargarClientes()
                Toast.makeText(this, "Cliente eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }

    /**
     * Inicia la actividad de pedidos para un cliente seleccionado.
     * @param cliente Cliente cuyos pedidos se desean ver.
     */
    private fun verPedidos(cliente: Cliente) {
        val intent = Intent(this, PedidosActivity::class.java)
        intent.putExtra("clienteId", cliente.id)
        startActivity(intent)
    }
}