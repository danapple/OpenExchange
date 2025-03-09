package com.danapple.openexchange.dto

import java.math.BigDecimal

data class CancelReplace (
    val behavior: BEHAVIOR,
    val originalClientOrderId: String,
    val price: BigDecimal,
    val quantity: Int,
    val legs: List<OrderLeg>
) {
    enum class BEHAVIOR {
        ALWAYS,  // Even if the original is DEAD
        CAP_AT_REMAINING_QUANTITY,  // Implies ONLY_IF_ALIVE
        ONLY_IF_ALIVE
    }
}