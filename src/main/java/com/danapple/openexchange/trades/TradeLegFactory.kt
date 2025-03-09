package com.danapple.openexchange.trades

import com.danapple.openexchange.orders.Order
import org.springframework.stereotype.Service

@Service
class TradeLegFactory(private val tradeLegIdGenerator: TradeLegIdGenerator) {
    fun createTradeLeg(order: Order, quantity: Int): TradeLeg {
        if (quantity == 0) {
            throw IllegalArgumentException("Quantity must be non-zero")
        }
        return TradeLeg(tradeLegIdGenerator.getId(), order, quantity)
    }
}