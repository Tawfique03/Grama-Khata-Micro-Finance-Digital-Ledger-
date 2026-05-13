package com.gramakhata.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gramakhata.app.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE customerId = :customerId ORDER BY timestamp DESC")
    fun getTransactionsForCustomer(customerId: Int): Flow<List<TransactionEntity>>
    
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'PAYMENT' AND timestamp >= :startTime AND timestamp < :endTime")
    fun getCollectionInRange(startTime: Long, endTime: Long): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'CREDIT'")
    fun getTotalCredit(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'PAYMENT'")
    fun getTotalPayment(): Flow<Double?>
}

