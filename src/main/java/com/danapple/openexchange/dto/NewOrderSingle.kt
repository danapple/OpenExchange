package com.danapple.openexchange.dto

import java.math.BigDecimal

data class NewOrderSingle (
    val instrumentId: Long,
    val price: BigDecimal,
    val quantity: Int
)