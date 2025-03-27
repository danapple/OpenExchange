package com.danapple.openexchange.entities.trades

import com.danapple.openexchange.dao.IdGenerator
import com.danapple.openexchange.orders.OrderState
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class TradeLegFactory(@Qualifier("tradeLegIdGenerator") private val tradeLegIdGenerator: IdGenerator) {
    fun createTradeLeg(orderState: OrderState, trade: Trade, quantity: Int): TradeLeg {
        if (quantity == 0) {
            throw IllegalArgumentException("Quantity must be non-zero")
        }
        val tradeLeg = TradeLeg(tradeLegIdGenerator.getNextId(), trade, orderState, quantity)
        orderState.addTradeLeg(tradeLeg)
        trade.addTradeLeg(tradeLeg)
        return tradeLeg
    }
}