package com.danapple.openexchange.trades

import java.math.BigDecimal
import java.time.Clock
import java.util.*

data class Trade private constructor(
    val tradeId: String,
    val timestamp: Long,
    val price: BigDecimal,
    val tradeLegs: Set<TradeLeg>
) {
    companion object TradeFactory {
        var clock: Clock = Clock.systemDefaultZone()

        fun createTrade(price: BigDecimal, tradeLegs: Set<TradeLeg>): Trade {
            var netQuantity = tradeLegs.sumOf { it.quantity.toLong() }
            if (netQuantity != 0L) {
                throw IllegalArgumentException(String.format("Trade net quantity must be 0, not %d", netQuantity))
            }
            return Trade(UUID.randomUUID().toString(), clock.millis(), price, tradeLegs)
        }
    }
}
