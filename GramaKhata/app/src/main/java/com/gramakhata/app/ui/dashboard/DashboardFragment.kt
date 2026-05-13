package com.gramakhata.app.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gramakhata.app.R
import com.gramakhata.app.data.local.AppDatabase
import com.gramakhata.app.ui.customers.CustomerAdapter
import com.gramakhata.app.ui.main.MainActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

class DashboardFragment : Fragment() {

    private val TAG = "DashboardFragment"
    private lateinit var adapter: CustomerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTotalDues = view.findViewById<TextView>(R.id.tv_total_dues)
        val tvTodayCollection = view.findViewById<TextView>(R.id.tv_today_collection)
        val tvTotalCustomers = view.findViewById<TextView>(R.id.tv_total_customers)
        val tvViewAll = view.findViewById<TextView>(R.id.tv_view_all_customers)
        val tvEmptyState = view.findViewById<TextView>(R.id.tv_empty_state)
        val rvCustomers = view.findViewById<RecyclerView>(R.id.rv_recent_customers)
        val btnAddTransaction = view.findViewById<View>(R.id.btn_add_transaction)
        val btnViewReports = view.findViewById<View>(R.id.btn_view_reports)

        val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        val db = AppDatabase.getDatabase(requireContext())

        adapter = CustomerAdapter { customerWithDue ->
            Log.d(TAG, "Customer clicked: ${customerWithDue.customer.name}")
            val bundle = Bundle().apply {
                putInt("customerId", customerWithDue.customer.id)
                putString("customerName", customerWithDue.customer.name)
            }
            findNavController().navigate(R.id.nav_ledger, bundle)
        }

        rvCustomers.layoutManager = LinearLayoutManager(requireContext())
        rvCustomers.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                db.customerDao().getAllCustomersWithDue().collectLatest { customers ->
                    Log.d(TAG, "Customers observed: ${customers.size}")
                    if (customers.isEmpty()) {
                        tvEmptyState.visibility = View.VISIBLE
                        rvCustomers.visibility = View.GONE
                    } else {
                        tvEmptyState.visibility = View.GONE
                        rvCustomers.visibility = View.VISIBLE
                    }
                    adapter.submitList(customers.take(5))
                    val totalDue = customers.sumOf { it.netDue }
                    tvTotalDues.text = formatter.format(totalDue)
                    tvTotalCustomers.text = customers.size.toString()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error observing customers", e)
            }
        }

        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfDay = cal.timeInMillis
        val endOfDay = startOfDay + 24 * 60 * 60 * 1000

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                db.transactionDao().getCollectionInRange(startOfDay, endOfDay).collectLatest { amount ->
                    tvTodayCollection.text = formatter.format(amount ?: 0.0)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error observing collection", e)
            }
        }

        btnAddTransaction.setOnClickListener {
            Log.d(TAG, "Add Transaction (Shortcut) Clicked")
            (activity as? MainActivity)?.showAddCustomerDialog()
        }

        btnViewReports.setOnClickListener {
            Log.d(TAG, "View Reports Clicked")
            findNavController().navigate(R.id.nav_reports)
        }

        tvViewAll.setOnClickListener {
            Log.d(TAG, "View All Clicked")
            findNavController().navigate(R.id.nav_customers)
        }
    }
}
