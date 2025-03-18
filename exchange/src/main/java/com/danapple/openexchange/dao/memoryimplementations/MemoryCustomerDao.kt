package com.danapple.openexchange.dao.memoryimplementations

import com.danapple.openexchange.entities.customers.Customer
import com.danapple.openexchange.dao.CustomerDao
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class MemoryCustomerDao : CustomerDao {
    override fun getCustomer(customerId: String) : Customer {
        return customers.computeIfAbsent(customerId, { Customer(customerId) } )
    }

    companion object {
        val customers = ConcurrentHashMap<String, Customer>()
    }
}