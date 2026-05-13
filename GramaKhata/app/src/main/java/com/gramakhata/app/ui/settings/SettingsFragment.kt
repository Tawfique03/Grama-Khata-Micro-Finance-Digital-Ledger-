package com.gramakhata.app.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gramakhata.app.R
import com.gramakhata.app.util.LocaleHelper

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.btn_profile).setOnClickListener {
            Toast.makeText(requireContext(), "Profile settings coming soon!", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.btn_backup).setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Backup & Restore")
                .setMessage("Local backup created at: /GramaKhata/backups/db_backup.sqlite\nCloud sync is available in Pro version.")
                .setPositiveButton("OK", null)
                .show()
        }

        view.findViewById<View>(R.id.btn_language).setOnClickListener {
            val languages = arrayOf("English", "Hindi", "Bengali", "Odia", "Telugu", "Kannada")
            val codes = arrayOf("en", "hi", "bn", "or", "te", "kn")
            
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select Language")
                .setItems(languages) { _, which ->
                    LocaleHelper.setLocale(requireContext(), codes[which])
                    // Restart activity to apply changes
                    val intent = requireActivity().intent
                    requireActivity().finish()
                    startActivity(intent)
                }
                .show()
        }
    }
}
