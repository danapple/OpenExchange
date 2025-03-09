package com.danapple.openexchange.dao

import com.danapple.openexchange.customers.Customer

interface CustomerDao {
    fun getCustomer(customerId: String): Customer
}