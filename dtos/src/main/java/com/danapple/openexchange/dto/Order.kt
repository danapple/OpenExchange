package com.danapple.openexchange.dto

import java.math.BigDecimal

data class Order (
    val clientOrderId: String,
    val price: BigDecimal,
    val quantity: Int,
    val legs: List<OrderLeg>,
)