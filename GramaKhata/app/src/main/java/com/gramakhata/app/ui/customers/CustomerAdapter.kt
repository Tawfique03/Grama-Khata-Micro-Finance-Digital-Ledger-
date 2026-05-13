package com.gramakhata.app.ui.customers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gramakhata.app.R
import com.gramakhata.app.data.local.dao.CustomerWithDue
import java.text.NumberFormat
import java.util.Locale

class CustomerAdapter(
    private val onItemClick: (CustomerWithDue) -> Unit
) : ListAdapter<CustomerWithDue, CustomerAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_customer_name)
        val tvLastPayment: TextView = view.findViewById(R.id.tv_last_payment)
        val tvNetDue: TextView = view.findViewById(R.id.tv_net_due)
        val tvStatusChip: TextView = view.findViewById(R.id.tv_status_chip)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_customer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

        holder.tvName.text = item.customer.name
        val due = item.netDue
        holder.tvNetDue.text = formatter.format(due)

        if (due > 0) {
            holder.tvStatusChip.text = "DUE"
            holder.tvStatusChip.setBackgroundResource(R.drawable.bg_pill_error)
        } else {
            holder.tvStatusChip.text = "SETTLED"
            holder.tvStatusChip.setBackgroundResource(R.drawable.bg_pill_success)
        }

        if (item.lastPaymentDate != null) {
            val daysAgo = ((System.currentTimeMillis() - item.lastPaymentDate) / (1000 * 60 * 60 * 24)).toInt()
            holder.tvLastPayment.text = when {
                daysAgo == 0 -> "Last payment: Today"
                daysAgo == 1 -> "Last payment: Yesterday"
                else -> "Last payment: $daysAgo days ago"
            }
        } else {
            holder.tvLastPayment.text = "No payments yet"
        }

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    class DiffCallback : DiffUtil.ItemCallback<CustomerWithDue>() {
        override fun areItemsTheSame(a: CustomerWithDue, b: CustomerWithDue) =
            a.customer.id == b.customer.id

        override fun areContentsTheSame(a: CustomerWithDue, b: CustomerWithDue) =
            a == b
    }
}
