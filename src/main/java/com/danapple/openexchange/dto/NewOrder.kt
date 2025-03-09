package com.danapple.openexchange.dto

import java.math.BigDecimal

data class NewOrder (
    val price: BigDecimal,
    val quantity: Int,
    val legs: List<OrderLeg>
)