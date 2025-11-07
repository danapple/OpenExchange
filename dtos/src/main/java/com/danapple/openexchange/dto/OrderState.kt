package com.danapple.openexchange.dto

data class OrderState (
    val updateTime: Long,
    val orderStatus: OrderStatus,
    val remainingQuantity: Int,
    val versionNumber: Int,
    val order: Order,
)
