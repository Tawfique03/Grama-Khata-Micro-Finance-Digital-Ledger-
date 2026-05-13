package com.gramakhata.app.data.repository

import com.gramakhata.app.data.local.dao.CustomerDao
import com.gramakhata.app.data.local.entity.CustomerEntity

class CustomerRepository(private val customerDao: CustomerDao) {
    val allCustomersWithDue = customerDao.getAllCustomersWithDue()

    suspend fun insert(customer: CustomerEntity) {
        customerDao.insert(customer)
    }

    suspend fun update(customer: CustomerEntity) {
        customerDao.update(customer)
    }

    suspend fun getCustomerById(id: Int) = customerDao.getCustomerById(id)
}

