package com.example.vaultix.act.transacciones

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vaultix.R
import com.example.vaultix.model.Transaction
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adaptador para manejar la lista de transacciones en un RecyclerView.
 *
 * @property transactions Lista de transacciones a mostrar.
 * @property onItemClick Callback que se ejecuta al hacer clic en una transacción.
 */
class TransactionsAdapter(
    private val transactions: List<Transaction>,
    private val onItemClick: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {

    /**
     * ViewHolder que contiene las vistas para un elemento de transacción.
     *
     * @param view Vista inflada para una transacción.
     */
    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val chipEstado: Chip = view.findViewById(R.id.chipEstado) // Chip para mostrar el estado de la transacción
    }

    /**
     * Infla el diseño para un elemento de transacción y crea un ViewHolder.
     *
     * @param parent Vista padre del RecyclerView.
     * @param viewType Tipo de vista (no utilizado aquí, ya que solo hay un tipo).
     * @return Un TransactionViewHolder con la vista inflada.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido, parent, false)
        return TransactionViewHolder(view)
    }

    /**
     * Vincula los datos de una transacción a un ViewHolder específico.
     *
     * @param holder TransactionViewHolder que se actualizará con los datos.
     * @param position Posición de la transacción en la lista.
     */
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaccion = transactions[position]
        holder.tvDescripcion.text = transaccion.categoria
        holder.tvFecha.text = formatearFecha(transaccion.fecha)
        holder.chipEstado.text = transaccion.tipo
        setEstadoColor(holder.chipEstado, transaccion.categoria)
        holder.itemView.setOnClickListener { onItemClick(transaccion) }
    }

    private fun formatearFecha(timestamp: String): String {
        val fecha = Date(timestamp.toLong())
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fecha)
    }

    /**
     * Configura el color del Chip según el estado de la transacción.
     *
     * @param chip Chip que mostrará el estado.
     * @param estado Estado de la transacción (ejemplo: "Pendiente", "Completado").
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
     * @return Número de transacciones en la lista.
     */
    override fun getItemCount() = transactions.size
}
