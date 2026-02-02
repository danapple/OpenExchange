package com.danapple.openexchange.dao.jdbcdao

import com.danapple.openexchange.dao.CustomerDao
import com.danapple.openexchange.entities.customers.Customer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

@Repository
open class CustomerDaoJdbcImpl(@Qualifier("customerJdbcClient") private val jdbcClient: JdbcClient) : CustomerDao {
    private val customersById = ConcurrentHashMap<Long, Customer>()
    private val customersByKey = ConcurrentHashMap<String, Customer>()

    init {
        setOf(
            Customer(0, "BrokerA"),
            Customer(1, "BrokerB"),
            Customer(1, "QuoterBST"),
        ).forEach { customer ->
            customersById[customer.customerId] = customer
            customersByKey[customer.customerKey] = customer
        }
    }

    override fun getCustomer(customerId: Long): Customer? {
        return customersById[customerId]
    }

    override fun getCustomer(customerKey: String): Customer? {
        return customersByKey[customerKey]
    }
}