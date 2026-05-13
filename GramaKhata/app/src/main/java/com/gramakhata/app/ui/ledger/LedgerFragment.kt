package com.gramakhata.app.ui.ledger

import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.gramakhata.app.R
import com.gramakhata.app.data.local.AppDatabase
import com.gramakhata.app.data.local.entity.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class LedgerFragment : Fragment() {

    private var customerId: Int = 0
    private var customerName: String = ""
    private var customerPhone: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ledger, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customerId = arguments?.getInt("customerId", 0) ?: 0
        customerName = arguments?.getString("customerName", "") ?: ""

        val db = AppDatabase.getDatabase(requireContext())
        
        // Fetch phone number
        viewLifecycleOwner.lifecycleScope.launch {
            db.customerDao().getCustomerById(customerId)?.let {
                customerPhone = it.phone
            }
        }

        val tvName = view.findViewById<TextView>(R.id.tv_customer_name)
        val tvNetDue = view.findViewById<TextView>(R.id.tv_net_due)
        val ivBack = view.findViewById<ImageView>(R.id.iv_back)
        val ivEdit = view.findViewById<ImageView>(R.id.iv_edit_customer)
        val ivDelete = view.findViewById<ImageView>(R.id.iv_delete_customer)
        val btnAddTxn = view.findViewById<MaterialButton>(R.id.btn_add_txn)
        val btnRemind = view.findViewById<MaterialButton>(R.id.btn_remind)
        val btnPdf = view.findViewById<MaterialButton>(R.id.btn_pdf)
        val rvTransactions = view.findViewById<RecyclerView>(R.id.rv_transactions)
        val tvEmptyTxn = view.findViewById<TextView>(R.id.tv_empty_txn)

        val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

        tvName.text = customerName
        var currentDue = 0.0

        val adapter = TransactionAdapter()
        rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        rvTransactions.adapter = adapter

        // Observe transactions
        viewLifecycleOwner.lifecycleScope.launch {
            db.transactionDao().getTransactionsForCustomer(customerId).collectLatest { transactions ->
                if (transactions.isEmpty()) {
                    tvEmptyTxn.visibility = View.VISIBLE
                    rvTransactions.visibility = View.GONE
                } else {
                    tvEmptyTxn.visibility = View.GONE
                    rvTransactions.visibility = View.VISIBLE
                }
                adapter.submitList(transactions)

                // Calculate net due
                val totalCredit = transactions.filter { it.type == "CREDIT" }.sumOf { it.amount }
                val totalPayment = transactions.filter { it.type == "PAYMENT" }.sumOf { it.amount }
                currentDue = totalCredit - totalPayment
                tvNetDue.text = formatter.format(currentDue)
            }
        }

        ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        ivEdit.setOnClickListener {
            showEditCustomerDialog(db)
        }

        ivDelete.setOnClickListener {
            showDeleteConfirmation(db)
        }

        btnRemind.setOnClickListener {
            showReminderOptions(currentDue)
        }

        btnPdf.setOnClickListener {
            exportToPdf(adapter.currentList)
        }

        btnAddTxn.setOnClickListener {
            showAddTransactionDialog(db)
        }
    }

    private fun showReminderOptions(due: Double) {
        if (due <= 0) {
            Toast.makeText(requireContext(), "No outstanding due", Toast.LENGTH_SHORT).show()
            return
        }
        val options = arrayOf("WhatsApp", "SMS")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Send Reminder via")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> sendWhatsAppReminder(due)
                    1 -> sendSMSReminder(due)
                }
            }
            .show()
    }

    private fun sendWhatsAppReminder(due: Double) {
        val cleanPhone = customerPhone.filter { it.isDigit() }
        val finalPhone = if (cleanPhone.length == 10) "91$cleanPhone" else cleanPhone
        val message = "Hi $customerName, this is a reminder from Grama Khata. Your outstanding due is ₹${String.format("%.2f", due)}. Please settle it at your convenience. Thank you!"
        val url = "https://api.whatsapp.com/send?phone=$finalPhone&text=${Uri.encode(message)}"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "WhatsApp not installed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendSMSReminder(due: Double) {
        val message = "Hi $customerName, this is a reminder from Grama Khata. Your outstanding due is ₹${String.format("%.2f", due)}. Please settle it at your convenience. Thank you!"
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("smsto:$customerPhone")
        intent.putExtra("sms_body", message)
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Could not open SMS app", Toast.LENGTH_SHORT).show()
        }
    }

    private fun exportToPdf(transactions: List<TransactionEntity>) {
        try {
            val pdfDocument = PdfDocument()
            val pageWidth = 595
            val pageHeight = 842
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint()

            // Header
            paint.textSize = 24f
            paint.isFakeBoldText = true
            paint.color = android.graphics.Color.BLACK
            canvas.drawText("Grama Khata Ledger", 40f, 60f, paint)
            
            paint.textSize = 14f
            paint.isFakeBoldText = false
            paint.color = android.graphics.Color.DKGRAY
            canvas.drawText("Customer: $customerName", 40f, 90f, paint)
            canvas.drawText("Phone: $customerPhone", 40f, 110f, paint)
            canvas.drawText("Date: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())}", 40f, 130f, paint)

            // Table Header
            var y = 180f
            paint.isFakeBoldText = true
            paint.color = android.graphics.Color.BLACK
            canvas.drawText("Date", 40f, y, paint)
            canvas.drawText("Type", 140f, y, paint)
            canvas.drawText("Description", 240f, y, paint)
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("Amount", 550f, y, paint)
            
            y += 10f
            paint.strokeWidth = 2f
            canvas.drawLine(40f, y, 550f, y, paint)
            
            y += 25f
            paint.isFakeBoldText = false
            paint.textAlign = Paint.Align.LEFT
            val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
            
            var totalCredit = 0.0
            var totalPayment = 0.0

            for (txn in transactions) {
                if (y > 780) break 
                canvas.drawText(dateFormat.format(Date(txn.timestamp)), 40f, y, paint)
                
                val typeColor = if (txn.type == "CREDIT") android.graphics.Color.RED else android.graphics.Color.parseColor("#006d3a")
                val originalColor = paint.color
                paint.color = typeColor
                canvas.drawText(txn.type, 140f, y, paint)
                paint.color = originalColor

                val desc = if (txn.description.length > 22) txn.description.take(19) + "..." else txn.description
                canvas.drawText(desc, 240f, y, paint)
                
                paint.textAlign = Paint.Align.RIGHT
                canvas.drawText("₹${String.format("%.2f", txn.amount)}", 550f, y, paint)
                paint.textAlign = Paint.Align.LEFT
                
                if (txn.type == "CREDIT") totalCredit += txn.amount else totalPayment += txn.amount
                
                y += 25f
            }

            // Summary
            y += 20f
            paint.strokeWidth = 1f
            canvas.drawLine(300f, y, 550f, y, paint)
            y += 30f
            paint.isFakeBoldText = true
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("Total Credit: ₹${String.format("%.2f", totalCredit)}", 550f, y, paint)
            y += 20f
            canvas.drawText("Total Payment: ₹${String.format("%.2f", totalPayment)}", 550f, y, paint)
            y += 25f
            paint.textSize = 18f
            canvas.drawText("Net Due: ₹${String.format("%.2f", totalCredit - totalPayment)}", 550f, y, paint)

            pdfDocument.finishPage(page)

            val fileName = "Statement_${customerName.replace(" ", "_")}.pdf"
            val file = File(requireContext().getExternalFilesDir(null), fileName)
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()

            Toast.makeText(requireContext(), "PDF Saved", Toast.LENGTH_SHORT).show()
            
            val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Share Statement"))

        } catch (e: Exception) {
            Log.e("LedgerFragment", "PDF Error", e)
            Toast.makeText(requireContext(), "Error generating PDF", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showEditCustomerDialog(db: AppDatabase) {
        viewLifecycleOwner.lifecycleScope.launch {
            val customer = db.customerDao().getCustomerById(customerId) ?: return@launch
            
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_customer, null)
            val etName = dialogView.findViewById<TextInputEditText>(R.id.et_customer_name)
            val etPhone = dialogView.findViewById<TextInputEditText>(R.id.et_customer_phone)
            val etAddress = dialogView.findViewById<TextInputEditText>(R.id.et_customer_address)
            val btnSave = dialogView.findViewById<View>(R.id.btn_save_customer)

            etName.setText(customer.name)
            etPhone.setText(customer.phone)
            etAddress.setText(customer.address)
            (btnSave as? MaterialButton)?.text = "Update Customer"

            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setTitle("Edit Customer")
                .create()

            btnSave.setOnClickListener {
                val name = etName.text?.toString()?.trim() ?: ""
                val phone = etPhone.text?.toString()?.trim() ?: ""
                val address = etAddress.text?.toString()?.trim() ?: ""

                if (name.isEmpty() || phone.isEmpty()) return@setOnClickListener

                CoroutineScope(Dispatchers.IO).launch {
                    db.customerDao().update(customer.copy(name = name, phone = phone, address = address))
                    activity?.runOnUiThread {
                        customerName = name
                        view?.findViewById<TextView>(R.id.tv_customer_name)?.text = name
                        customerPhone = phone
                        Toast.makeText(requireContext(), "Customer updated", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            }
            dialog.show()
        }
    }

    private fun showDeleteConfirmation(db: AppDatabase) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Customer")
            .setMessage("Are you sure you want to delete $customerName and all their transactions? This cannot be undone.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    val customer = db.customerDao().getCustomerById(customerId)
                    if (customer != null) {
                        db.customerDao().delete(customer)
                        activity?.runOnUiThread {
                            Toast.makeText(requireContext(), "Customer deleted", Toast.LENGTH_SHORT).show()
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        }
                    }
                }
            }
            .show()
    }

    private fun showAddTransactionDialog(db: AppDatabase) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_transaction, null)
        val tvCustName = dialogView.findViewById<TextView>(R.id.tv_customer_name)
        val etAmount = dialogView.findViewById<TextInputEditText>(R.id.et_amount)
        val etDesc = dialogView.findViewById<TextInputEditText>(R.id.et_description)
        val btnPayment = dialogView.findViewById<MaterialButton>(R.id.btn_payment)
        val btnCredit = dialogView.findViewById<MaterialButton>(R.id.btn_credit)

        tvCustName.text = "For: $customerName"

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_card_surface)

        fun saveTransaction(type: String) {
            val amountStr = etAmount.text?.toString()?.trim() ?: ""
            if (amountStr.isEmpty()) {
                etAmount.error = "Enter amount"
                return
            }
            val amount = amountStr.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                etAmount.error = "Invalid amount"
                return
            }
            val desc = etDesc.text?.toString()?.trim() ?: ""

            CoroutineScope(Dispatchers.IO).launch {
                db.transactionDao().insert(
                    TransactionEntity(
                        customerId = customerId,
                        amount = amount,
                        type = type,
                        description = desc
                    )
                )
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "$type recorded!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        }

        btnPayment.setOnClickListener { saveTransaction("PAYMENT") }
        btnCredit.setOnClickListener { saveTransaction("CREDIT") }

        dialog.show()
    }
}
