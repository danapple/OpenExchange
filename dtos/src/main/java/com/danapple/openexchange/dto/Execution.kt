package com.danapple.openexchange.dto

import java.math.BigDecimal

data class Execution (
    val clientOrderId: String,
    val instrumentId: Long,
    val createTime: Long,
    val price: BigDecimal,
    val quantity: Int
)