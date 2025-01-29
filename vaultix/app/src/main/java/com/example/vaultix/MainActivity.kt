package com.example.vaultix
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {
    private lateinit var clienteRepository: ClienteRepository
    private lateinit var clienteAdapter: ClienteAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clienteRepository = ClienteRepository(this)
        recyclerView = findViewById(R.id.recyclerViewClientes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadClientes()
    }

    private fun loadClientes() {
        val clientes = clienteRepository.getAllClientes()
        clienteAdapter = ClienteAdapter(clientes, { clienteId -> onVerTransacciones(clienteId) },
            { cliente -> onEditarCliente(cliente) },
            { clienteId -> onBorrarCliente(clienteId) })
        recyclerView.adapter = clienteAdapter
    }

    private fun onVerTransacciones(clienteId: Int) {
        val intent = Intent(this, TransaccionesActivity::class.java)
        intent.putExtra("clienteId", clienteId)
        startActivity(intent)
    }

    private fun onEditarCliente(cliente: Cliente) {
        val intent = Intent(this, EditarClienteActivity::class.java)
        intent.putExtra("clienteId", cliente.id)
        intent.putExtra("clienteNombre", cliente.nombre)
        startActivity(intent)
    }

    private fun onBorrarCliente(clienteId: Int) {
        clienteRepository.deleteCliente(clienteId)
        Toast.makeText(this, "Cliente eliminado", Toast.LENGTH_SHORT).show()
        loadClientes()
    }

    override fun onResume() {
        super.onResume()
        loadClientes()
    }
}
