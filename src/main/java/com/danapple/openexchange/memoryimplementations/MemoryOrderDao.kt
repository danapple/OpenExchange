package com.danapple.openexchange.memoryimplementations

import com.danapple.openexchange.customers.Customer
import com.danapple.openexchange.dao.OrderDao
import com.danapple.openexchange.orders.OrderState
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException
import java.util.concurrent.ConcurrentHashMap

@Service
class MemoryOrderDao : OrderDao {
    val orders = ConcurrentHashMap<Customer, MutableMap<String, OrderState>>()
    override fun saveOrder(orderState: OrderState) {
        if (orders.computeIfAbsent(orderState.order.customer, { ConcurrentHashMap() }).putIfAbsent(orderState.order.clientOrderId, orderState) != null) {
            throw IllegalArgumentException("Order ${orderState.order.clientOrderId} already exists for customer ${orderState.order.customer}")
        }
    }

    override fun getOrder(customer: Customer, clientOrderId: String): OrderState? {
        return orders[customer]?.get(clientOrderId)
    }
}