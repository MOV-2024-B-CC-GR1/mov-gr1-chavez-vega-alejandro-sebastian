package com.example.vaultix

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class PedidosActivity : AppCompatActivity() {

    /**
     * Actividad para gestionar los pedidos de un cliente específico.
     * Permite visualizar, agregar, actualizar y eliminar pedidos.
     */
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerViewPedidos: RecyclerView
    private lateinit var adapter: PedidosAdapter
    private val pedidosList = mutableListOf<Pedido>()
    private var clienteId: Int = 0

    /**
     * Método llamado al crear la actividad.
     * Configura el RecyclerView, el botón flotante y carga los datos del cliente.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transactions)

        dbHelper = DatabaseHelper(this)
        recyclerViewPedidos = findViewById(R.id.recyclerViewPedidos)
        val fabAddPedido: ExtendedFloatingActionButton = findViewById(R.id.fabAddPedido)

        // Obtener el ID del cliente desde el Intent
        clienteId = intent.getIntExtra("clienteId", 0)

        setupRecyclerView()
        // Configurar el botón flotante para agregar un nuevo pedido
        fabAddPedido.setOnClickListener {
            val intent = Intent(this, PedidoFormActivity::class.java)
            intent.putExtra("clienteId", clienteId) // Pasar el ID del cliente al formulario
            startActivity(intent)
        }
    }

    /**
     * Configura el RecyclerView para mostrar la lista de pedidos.
     */
    private fun setupRecyclerView() {
        adapter = PedidosAdapter(pedidosList) { pedido ->
            mostrarOpcionesPedido(pedido)
        }
        recyclerViewPedidos.layoutManager = LinearLayoutManager(this)
        recyclerViewPedidos.adapter = adapter
    }

    /**
     * Método llamado cuando la actividad vuelve a primer plano.
     * Carga los pedidos desde la base de datos y actualiza la lista.
     */
    override fun onResume() {
        super.onResume()
        cargarPedidos()
    }

    /**
     * Carga los pedidos del cliente desde la base de datos y actualiza el RecyclerView.
     */
    private fun cargarPedidos() {
        pedidosList.clear()
        val cursor = dbHelper.obtenerPedidosPorCliente(clienteId)
        if (cursor.moveToFirst()) {
            do {
                val pedido = Pedido(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.PEDIDO_ID)),
                    cliente_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.PEDIDO_CLIENTE_ID)),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PEDIDO_DESCRIPCION)),
                    estado = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PEDIDO_ESTADO)),
                    fechaPedido = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PEDIDO_FECHA_PEDIDO))
                )
                pedidosList.add(pedido)
            } while (cursor.moveToNext())
        }
        cursor.close()
        adapter.notifyDataSetChanged()
    }

    /**
     * Muestra un cuadro de diálogo con opciones para un pedido seleccionado.
     * @param pedido Pedido seleccionado.
     */
    private fun mostrarOpcionesPedido(pedido: Pedido) {
        val opciones = arrayOf("Actualizar", "Eliminar")
        AlertDialog.Builder(this)
            .setTitle("Opciones para el pedido")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> actualizarPedido(pedido)
                    1 -> eliminarPedido(pedido)
                }
            }
            .show()
    }

    /**
     * Inicia la actividad de formulario para actualizar un pedido.
     * @param pedido Pedido que se desea actualizar.
     */
    private fun actualizarPedido(pedido: Pedido) {
        val intent = Intent(this, PedidoFormActivity::class.java)
        intent.putExtra("pedidoId", pedido.id)
        intent.putExtra("clienteId", pedido.cliente_id)
        startActivity(intent)
    }

    /**
     * Muestra un cuadro de diálogo para confirmar la eliminación de un pedido.
     * @param pedido Pedido que se desea eliminar.
     */
    private fun eliminarPedido(pedido: Pedido) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de eliminar este pedido?")
            .setPositiveButton("Sí") { _, _ ->
                dbHelper.eliminarPedido(pedido.id)
                cargarPedidos()
                Toast.makeText(this, "Pedido eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }
}