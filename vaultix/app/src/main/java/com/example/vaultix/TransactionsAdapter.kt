package com.example.vaultix

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Color
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adaptador para manejar la lista de pedidos en un RecyclerView.
 *
 * @property pedidos Lista de pedidos a mostrar.
 * @property onItemClick Callback que se ejecuta al hacer clic en un pedido.
 */
class PedidosAdapter(
    private val pedidos: List<Pedido>,
    private val onItemClick: (Pedido) -> Unit
) : RecyclerView.Adapter<PedidosAdapter.PedidoViewHolder>() {

    /**
     * ViewHolder que contiene las vistas para un elemento de pedido.
     *
     * @param view Vista inflada para un pedido.
     */
    class PedidoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val chipEstado: Chip = view.findViewById(R.id.chipEstado) // Chip para mostrar el estado del pedido
    }

    /**
     * Infla el diseño para un elemento de pedido y crea un ViewHolder.
     *
     * @param parent Vista padre del RecyclerView.
     * @param viewType Tipo de vista (no utilizado aquí, ya que solo hay un tipo).
     * @return Un PedidoViewHolder con la vista inflada.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transactions, parent, false)
        return PedidoViewHolder(view)
    }

    /**
     * Vincula los datos de un pedido a un ViewHolder específico.
     *
     * @param holder PedidoViewHolder que se actualizará con los datos.
     * @param position Posición del pedido en la lista.
     */
    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = pedidos[position]
        holder.tvDescripcion.text = pedido.descripcion
        holder.tvFecha.text = formatearFecha(pedido.fechaPedido)
        holder.chipEstado.text = pedido.estado
        setEstadoColor(holder.chipEstado, pedido.estado)
        holder.itemView.setOnClickListener { onItemClick(pedido) }
    }

    private fun formatearFecha(timestamp: String): String {
        val fecha = Date(timestamp.toLong())
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fecha)
    }

    /**
     * Configura el color del Chip según el estado del pedido.
     *
     * @param chip Chip que mostrará el estado.
     * @param estado Estado del pedido (ejemplo: "Pendiente", "Completado").
     */
    private fun setEstadoColor(chip: Chip, estado: String) {
        val colorRes = when(estado.lowercase()) {
            "pendiente" -> R.color.estado_pendiente
            "en proceso" -> R.color.estado_proceso
            "completado" -> R.color.estado_completado
            "cancelado" -> R.color.estado_cancelado
            else -> R.color.gray
        }
        chip.setChipBackgroundColorResource(colorRes)
    }

    /**
     * Devuelve el número total de elementos en la lista.
     *
     * @return Número de pedidos en la lista.
     */
    override fun getItemCount() = pedidos.size
}