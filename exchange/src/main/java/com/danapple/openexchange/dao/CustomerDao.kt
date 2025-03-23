package com.danapple.openexchange.dao

import com.danapple.openexchange.entities.customers.Customer

interface CustomerDao {
    fun getCustomer(customerId: Long): Customer?

    fun getCustomer(customerKey: String): Customer?
}