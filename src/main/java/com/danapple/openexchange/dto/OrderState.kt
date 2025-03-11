package com.danapple.openexchange.dto

import com.danapple.openexchange.orders.OrderStatus

data class OrderState (
    val timeStamp: Long,
    val orderStatus: OrderStatus,
    val remainingQuantity: Int,
    val order: Order
)
