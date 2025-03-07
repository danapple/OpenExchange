package com.danapple.openexchange.trades

import com.danapple.openexchange.orders.Order

data class TradeLeg private constructor(
    val order: Order,
    val quantity: Int
) {
    companion object TradeLegFactory {
        fun createTradeLeg(order: Order, quantity: Int): TradeLeg {
            if (quantity == 0) {
                throw IllegalArgumentException("Quantity must be non-zero")
            }
            return TradeLeg(order, quantity)
        }
    }
}
