package com.danapple.openexchange.dao.memoryimplementations

import com.danapple.openexchange.entities.customers.Customer
import com.danapple.openexchange.dao.OrderDao
import com.danapple.openexchange.orders.OrderState
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class MemoryOrderDao : OrderDao {
    val orders = ConcurrentHashMap<Customer, MutableMap<String, OrderState>>()
    override fun saveOrder(orderState: OrderState) {
        orders.computeIfAbsent(orderState.order.customer) { ConcurrentHashMap() }[orderState.order.clientOrderId] = orderState
    }

    override fun getOrder(customer: Customer, clientOrderId: String): OrderState? {
        return orders[customer]?.get(clientOrderId)
    }

    override fun getOrders(customer: Customer): Collection<OrderState> {
        return orders[customer]?.values ?: emptySet()
    }
}
