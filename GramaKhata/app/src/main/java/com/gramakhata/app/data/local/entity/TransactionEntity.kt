package com.gramakhata.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerId: Int,
    val amount: Double,
    val type: String, // "CREDIT" or "PAYMENT"
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

