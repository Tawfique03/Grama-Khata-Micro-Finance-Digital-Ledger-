package com.gramakhata.app.data.repository

import com.gramakhata.app.data.local.dao.TransactionDao
import com.gramakhata.app.data.local.entity.TransactionEntity

class TransactionRepository(private val transactionDao: TransactionDao) {
    suspend fun insert(transaction: TransactionEntity) {
        transactionDao.insert(transaction)
    }

    fun getTransactionsForCustomer(customerId: Int) = transactionDao.getTransactionsForCustomer(customerId)
    
    fun getCollectionInRange(startTime: Long, endTime: Long) = transactionDao.getCollectionInRange(startTime, endTime)
}

