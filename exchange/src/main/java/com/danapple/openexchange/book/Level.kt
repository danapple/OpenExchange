package com.danapple.openexchange.book

import com.danapple.openexchange.orders.OrderState

internal class Level {
    private val orders = LinkedHashSet<OrderState>()

    internal fun addOrder(orderState: OrderState) {
        orders.add(orderState)
    }

    internal fun getOrderStates(): List<OrderState> {
        return orders.toList()
    }

    internal fun removeOrder(orderState: OrderState): Boolean {
        orders.remove(orderState)
        return orders.size == 0
    }
}