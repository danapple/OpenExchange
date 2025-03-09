package com.danapple.openexchange.dto

import com.danapple.openexchange.orders.OrderStatus
import java.math.BigDecimal

data class Order (
    val timeStamp: Long,
    val orderStatus: OrderStatus,
    val price: BigDecimal,
    val quantity: Int,
    val remainingQuantity: Int,
    val legs: List<OrderLeg>,
)