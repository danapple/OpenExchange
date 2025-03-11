package com.danapple.openexchange.dao

import com.danapple.openexchange.customers.Customer
import com.danapple.openexchange.orders.OrderState

interface OrderDao {
    fun saveOrder(orderState: OrderState)

    fun getOrder(customer: Customer, clientOrderId: String) : OrderState?

    fun getOrders(customer: Customer): Collection<OrderState>

}