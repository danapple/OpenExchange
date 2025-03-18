package com.danapple.openexchange.entities.trades

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class TradeFactory (@Autowired val tradeIdGenerator: TradeIdGenerator) {
    fun createTrade(timestamp: Long, price: BigDecimal, tradeLegs: Set<TradeLeg>): Trade {
        var netQuantity = tradeLegs.sumOf { it.quantity.toLong() }
        if (netQuantity != 0L) {
            throw IllegalArgumentException("Trade net quantity must be 0, not $netQuantity")
        }
        return Trade(tradeIdGenerator.getId(), timestamp, price, tradeLegs)
    }
}