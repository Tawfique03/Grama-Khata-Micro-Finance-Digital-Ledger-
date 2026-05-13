package com.gramakhata.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Embedded
import com.gramakhata.app.data.local.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow

data class CustomerWithDue(
    @Embedded val customer: CustomerEntity,
    val totalCredit: Double,
    val totalPayment: Double,
    val lastPaymentDate: Long?
) {
    val netDue: Double get() = totalCredit - totalPayment
}

@Dao
interface CustomerDao {
    @Insert
    suspend fun insert(customer: CustomerEntity): Long

    @Update
    suspend fun update(customer: CustomerEntity)

    @Query("""
        SELECT c.*, 
        COALESCE(SUM(CASE WHEN t.type = 'CREDIT' THEN t.amount ELSE 0 END), 0) as totalCredit,
        COALESCE(SUM(CASE WHEN t.type = 'PAYMENT' THEN t.amount ELSE 0 END), 0) as totalPayment,
        MAX(CASE WHEN t.type = 'PAYMENT' THEN t.timestamp ELSE null END) as lastPaymentDate
        FROM customers c
        LEFT JOIN transactions t ON c.id = t.customerId
        GROUP BY c.id
        ORDER BY c.createdAt DESC
    """)
    fun getAllCustomersWithDue(): Flow<List<CustomerWithDue>>

    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getCustomerById(id: Int): CustomerEntity?

    @androidx.room.Delete
    suspend fun delete(customer: CustomerEntity)
}

