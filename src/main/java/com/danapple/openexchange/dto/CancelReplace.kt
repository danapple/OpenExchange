package com.danapple.openexchange.dto

import java.math.BigDecimal

data class CancelReplace (
    val behavior: BEHAVIOR,
    val capping: CAPPING,
    val originalClientOrderId: String,
    val price: BigDecimal,
    val quantity: Int,
    val legs: List<OrderLeg>
) {
    enum class CAPPING {
        CAP_AT_REMAINING_QUANTITY,
        UNCAPPED
    }
    enum class BEHAVIOR {
        EVEN_IF_FILLED,
        ONLY_IF_OPEN
    }
}