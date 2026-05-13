package com.gramakhata.app.ui.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.gramakhata.app.R
import com.gramakhata.app.data.local.AppDatabase
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

class ReportsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reports, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTotalOutstanding = view.findViewById<TextView>(R.id.tv_total_outstanding)
        val tvWeeklyCollection = view.findViewById<TextView>(R.id.tv_weekly_collection)
        val tvMonthlyCollection = view.findViewById<TextView>(R.id.tv_monthly_collection)
        val tvDailyCollection = view.findViewById<TextView>(R.id.tv_daily_collection)
        val progressToday = view.findViewById<LinearProgressIndicator>(R.id.progress_today)
        val tvTodayPercent = view.findViewById<TextView>(R.id.tv_today_percent)

        val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        val db = AppDatabase.getDatabase(requireContext())

        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        
        val startOfDay = cal.timeInMillis
        val endOfDay = startOfDay + 24 * 60 * 60 * 1000

        // Weekly range
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        val startOfWeek = cal.timeInMillis

        // Monthly range
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val startOfMonth = cal.timeInMillis
        val now = System.currentTimeMillis()

        viewLifecycleOwner.lifecycleScope.launch {
            // Observe Total Outstanding
            combine(
                db.transactionDao().getTotalCredit(),
                db.transactionDao().getTotalPayment()
            ) { credit, payment ->
                (credit ?: 0.0) - (payment ?: 0.0)
            }.collect { outstanding ->
                tvTotalOutstanding.text = formatter.format(outstanding)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            // Weekly collection
            db.transactionDao().getCollectionInRange(startOfWeek, now).collect { amount ->
                tvWeeklyCollection.text = formatter.format(amount ?: 0.0)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            // Monthly collection
            db.transactionDao().getCollectionInRange(startOfMonth, now).collect { amount ->
                tvMonthlyCollection.text = formatter.format(amount ?: 0.0)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            // Daily collection and target mockup
            db.transactionDao().getCollectionInRange(startOfDay, endOfDay).collect { amount ->
                val collected = amount ?: 0.0
                tvDailyCollection.text = "Collected Today: ${formatter.format(collected)}"
                
                // Mock target of 5000 for visualization
                val target = 5000.0
                val percent = if (collected > target) 100 else ((collected / target) * 100).toInt()
                progressToday.progress = percent
                tvTodayPercent.text = "$percent%"
            }
        }
    }
}
