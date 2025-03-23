package com.danapple.openexchange.dao

import com.danapple.openexchange.entities.customers.Customer
import com.danapple.openexchange.orders.OrderState

interface OrderQueryDao {
    fun getOrder(customer: Customer, clientOrderId: String) : OrderState?

    fun getOrders(customer: Customer): Collection<OrderState>

    fun getOpenOrders(): Collection<OrderState>
}