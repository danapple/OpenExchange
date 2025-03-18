package com.danapple.openexchange.entities.trades

import com.danapple.openexchange.orders.Order
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TradeLegFactory(@Autowired val tradeLegIdGenerator: TradeLegIdGenerator) {
    fun createTradeLeg(order: Order, quantity: Int): TradeLeg {
        if (quantity == 0) {
            throw IllegalArgumentException("Quantity must be non-zero")
        }
        return TradeLeg(tradeLegIdGenerator.getId(), order, quantity)
    }
}