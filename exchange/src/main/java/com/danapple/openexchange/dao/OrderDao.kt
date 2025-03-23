package com.danapple.openexchange.dao

import com.danapple.openexchange.orders.OrderState

interface OrderDao {
    fun saveOrder(orderState: OrderState)
    fun updateOrder(orderState: OrderState)
}