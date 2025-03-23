package com.danapple.openexchange.entities.trades

import com.danapple.openexchange.orders.OrderState
import org.springframework.stereotype.Service

@Service
class TradeLegFactory(private val tradeLegIdGenerator: TradeLegIdGenerator) {
    fun createTradeLeg(orderState: OrderState, trade: Trade, quantity: Int): TradeLeg {
        if (quantity == 0) {
            throw IllegalArgumentException("Quantity must be non-zero")
        }
        val tradeLeg = TradeLeg(tradeLegIdGenerator.getId(), trade, orderState, quantity)
        orderState.addTradeLeg(tradeLeg)
        trade.addTradeLeg(tradeLeg)
        return tradeLeg
    }
}