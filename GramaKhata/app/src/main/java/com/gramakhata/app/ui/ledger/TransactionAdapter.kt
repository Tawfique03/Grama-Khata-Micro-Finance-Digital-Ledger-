package com.gramakhata.app.ui.ledger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gramakhata.app.R
import com.gramakhata.app.data.local.entity.TransactionEntity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionAdapter : ListAdapter<TransactionEntity, TransactionAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvType: TextView = view.findViewById(R.id.tv_txn_type)
        val tvDesc: TextView = view.findViewById(R.id.tv_txn_desc)
        val tvAmount: TextView = view.findViewById(R.id.tv_txn_amount)
        val tvDate: TextView = view.findViewById(R.id.tv_txn_date)
        val viewIndicator: View = view.findViewById(R.id.view_type_indicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        val ctx = holder.itemView.context

        holder.tvType.text = item.type.lowercase()
            .replaceFirstChar { it.uppercase() }
        holder.tvDesc.text = if (item.description.isNotEmpty()) item.description else "No description"
        holder.tvDate.text = dateFormat.format(Date(item.timestamp))

        if (item.type == "PAYMENT") {
            holder.tvAmount.text = "+${formatter.format(item.amount)}"
            holder.tvAmount.setTextColor(ContextCompat.getColor(ctx, R.color.secondary))
            holder.viewIndicator.setBackgroundColor(ContextCompat.getColor(ctx, R.color.secondary_container))
        } else {
            holder.tvAmount.text = "-${formatter.format(item.amount)}"
            holder.tvAmount.setTextColor(ContextCompat.getColor(ctx, R.color.error))
            holder.viewIndicator.setBackgroundColor(ContextCompat.getColor(ctx, R.color.error_container))
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<TransactionEntity>() {
        override fun areItemsTheSame(a: TransactionEntity, b: TransactionEntity) = a.id == b.id
        override fun areContentsTheSame(a: TransactionEntity, b: TransactionEntity) = a == b
    }
}
