package com.example.vaultix.ui.clientes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ccgr12024b_gasm.R

import com.example.vaultix.model.Cliente

/**
 * Adaptador para manejar la lista de clientes en un RecyclerView.
 *
 * @property clientes Lista de clientes que se mostrarán.
 * @property onItemClick Callback que se ejecuta al hacer clic en un cliente.
 */
class ClientesAdapter(
    private val clientes: List<Cliente>,
    private val onItemClick: (Cliente) -> Unit
) : RecyclerView.Adapter<ClientesAdapter.ClienteViewHolder>() {

    /**
     * ViewHolder que contiene las vistas de un elemento de cliente.
     *
     * @param view Vista inflada para un cliente.
     */
    class ClienteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvEmail: TextView = view.findViewById(R.id.tvEmail)
        val tvTelefono: TextView = view.findViewById(R.id.tvTelefono)
        val ivPremium: ImageView = view.findViewById(R.id.swPremium) // Icono para clientes premium
    }

    /**
     * Crea el ViewHolder inflando el diseño del elemento de cliente.
     *
     * @param parent Vista padre del RecyclerView.
     * @param viewType Tipo de vista (no utilizado aquí, ya que solo hay un tipo).
     * @return Un ClienteViewHolder con la vista inflada.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cliente, parent, false)
        return ClienteViewHolder(view)
    }

    /**
     * Vincula los datos de un cliente a un ViewHolder específico.
     *
     * @param holder ClienteViewHolder que se actualizará con los datos.
     * @param position Posición del cliente en la lista.
     */
    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        val cliente = clientes[position]
        holder.tvNombre.text = cliente.nombre
        holder.tvEmail.text = cliente.email
        holder.tvTelefono.text = cliente.telefono

        // Si el cliente es premium, mostramos el icono e incluso podemos cambiar el color del nombre
        if (cliente.premium) {
            holder.ivPremium.visibility = View.VISIBLE
            holder.tvNombre.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.estado_completado))
        } else {
            holder.ivPremium.visibility = View.GONE
            holder.tvNombre.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
        }

        holder.itemView.setOnClickListener { onItemClick(cliente) }
    }

    /**
     * Devuelve el número total de elementos en la lista.
     *
     * @return Número de clientes en la lista.
     */
    override fun getItemCount() = clientes.size
}
