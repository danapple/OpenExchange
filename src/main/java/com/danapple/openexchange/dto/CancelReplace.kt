package com.danapple.openexchange.dto

data class CancelReplace (
    val behavior: BEHAVIOR,
    val capping: CAPPING,
    val originalClientOrderId: String,
    val order: Order
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