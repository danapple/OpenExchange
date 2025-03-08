package com.danapple.openexchange.dto

import java.math.BigDecimal

data class CancelReplace (
    val originalClientOrderId: String,
    val instrumentId: Long,
    val price: BigDecimal,
    val quantity: Int
)