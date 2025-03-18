package com.danapple.openexchange.dto

import java.math.BigDecimal

data class Execution (
    val clientOrderId: String,
    val quantity: Int,         // Quantity of the trade (positive or negative)
    val price: BigDecimal      // Price at which the trade was executed
)