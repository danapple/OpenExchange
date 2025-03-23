package com.danapple.openexchange.dao.jdbcdao

import com.danapple.openexchange.entities.customers.Customer
import com.danapple.openexchange.orders.OrderState
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class OrderCache {
    private val ordersByCustomer = ConcurrentHashMap<Customer, MutableMap<String, OrderState>>()

    fun addOrder(orderState : OrderState) {
        ordersByCustomer
            .computeIfAbsent(orderState.order.customer,
                { ConcurrentHashMap<String, OrderState>() })[orderState.order.clientOrderId] = orderState
    }

    fun getOrder(customer : Customer, clientOrderId : String) : OrderState? {
        return ordersByCustomer[customer]?.get(clientOrderId)
    }

    fun getOrders(customer : Customer): Collection<OrderState> {
        return ordersByCustomer.getOrDefault(customer, emptyMap()).values
    }
}