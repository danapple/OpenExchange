package com.danapple.openexchange.dto

data class OrderState (
    val timeStamp: Long,
    val orderStatus: OrderStatus,
    val remainingQuantity: Int,
    val order: Order
)
