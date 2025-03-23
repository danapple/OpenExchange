package com.danapple.openexchange.dto

data class OrderState (
    val createTime: Long,
    val orderStatus: OrderStatus,
    val remainingQuantity: Int,
    val order: Order
)
