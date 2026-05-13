package com.gramakhata.app.ui.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.gramakhata.app.R
import com.gramakhata.app.data.local.AppDatabase
import com.gramakhata.app.data.local.entity.CustomerEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.widget.Toast
import android.content.Context
import com.gramakhata.app.util.LocaleHelper

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Toast.makeText(this, "Grama Khata 2.0 Loaded", Toast.LENGTH_SHORT).show()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val fab = findViewById<FloatingActionButton>(R.id.fab_add)

        try {
            // Give the fragment host a moment to initialize
            supportFragmentManager.executePendingTransactions()
            val navController = findNavController(R.id.nav_host_fragment)
            bottomNav.setupWithNavController(navController)

            navController.addOnDestinationChangedListener { _, destination, _ ->
                Log.d(TAG, "Navigated to: ${destination.label}")
                when (destination.id) {
                    R.id.nav_home, R.id.nav_customers -> fab.show()
                    else -> fab.hide()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Navigation setup failed", e)
            Toast.makeText(this, "Navigation error: ${e.message}", Toast.LENGTH_LONG).show()
        }

        fab.setOnClickListener {
            Log.d(TAG, "FAB Clicked")
            showAddCustomerDialog()
        }
    }

    fun showAddCustomerDialog() {
        try {
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_customer, null)
            val etName = dialogView.findViewById<TextInputEditText>(R.id.et_customer_name)
            val etPhone = dialogView.findViewById<TextInputEditText>(R.id.et_customer_phone)
            val etAddress = dialogView.findViewById<TextInputEditText>(R.id.et_customer_address)
            val btnSave = dialogView.findViewById<View>(R.id.btn_save_customer)

            val dialog = MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .create()

            dialog.window?.setBackgroundDrawableResource(R.drawable.bg_card_surface)

            btnSave.setOnClickListener {
                val name = etName.text?.toString()?.trim() ?: ""
                val phone = etPhone.text?.toString()?.trim() ?: ""
                val address = etAddress.text?.toString()?.trim() ?: ""

                if (name.isEmpty()) {
                    etName.error = "Name is required"
                    return@setOnClickListener
                }
                if (phone.isEmpty()) {
                    etPhone.error = "Phone is required"
                    return@setOnClickListener
                }

                val db = AppDatabase.getDatabase(applicationContext)
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        db.customerDao().insert(
                            CustomerEntity(name = name, phone = phone, address = address)
                        )
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Customer '$name' added!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to save customer", e)
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            dialog.show()
        } catch (e: Exception) {
            Log.e(TAG, "Dialog inflation failed", e)
            Toast.makeText(this, "UI Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
