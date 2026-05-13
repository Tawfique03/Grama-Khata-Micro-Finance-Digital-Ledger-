package com.gramakhata.app.ui.customers

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gramakhata.app.R
import com.gramakhata.app.data.local.AppDatabase
import com.gramakhata.app.data.local.dao.CustomerWithDue
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CustomersFragment : Fragment() {

    private lateinit var adapter: CustomerAdapter
    private var allCustomers: List<CustomerWithDue> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_customers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvCustomers = view.findViewById<RecyclerView>(R.id.rv_customers)
        val layoutEmpty = view.findViewById<LinearLayout>(R.id.layout_empty)
        val etSearch = view.findViewById<EditText>(R.id.et_search)
        val btnAddCustomer = view.findViewById<View>(R.id.btn_add_customer)

        val db = AppDatabase.getDatabase(requireContext())

        adapter = CustomerAdapter { customerWithDue ->
            val bundle = Bundle().apply {
                putInt("customerId", customerWithDue.customer.id)
                putString("customerName", customerWithDue.customer.name)
            }
            findNavController().navigate(R.id.nav_ledger, bundle)
        }

        rvCustomers.layoutManager = LinearLayoutManager(requireContext())
        rvCustomers.adapter = adapter

        // Observe customers
        viewLifecycleOwner.lifecycleScope.launch {
            db.customerDao().getAllCustomersWithDue().collectLatest { customers ->
                allCustomers = customers
                updateList(etSearch.text?.toString() ?: "")

                if (customers.isEmpty()) {
                    layoutEmpty.visibility = View.VISIBLE
                    rvCustomers.visibility = View.GONE
                } else {
                    layoutEmpty.visibility = View.GONE
                    rvCustomers.visibility = View.VISIBLE
                }
            }
        }

        // Search filter
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateList(s?.toString() ?: "")
            }
        })

        // Add Customer button
        btnAddCustomer.setOnClickListener {
            (activity as? com.gramakhata.app.ui.main.MainActivity)?.let { mainActivity ->
                mainActivity.findViewById<View>(R.id.fab_add)?.performClick()
            }
        }
    }

    private fun updateList(query: String) {
        val filtered = if (query.isEmpty()) {
            allCustomers
        } else {
            allCustomers.filter {
                it.customer.name.contains(query, ignoreCase = true) ||
                it.customer.phone.contains(query)
            }
        }
        adapter.submitList(filtered)
    }
}
