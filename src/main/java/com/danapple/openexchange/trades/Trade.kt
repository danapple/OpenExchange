package com.danapple.openexchange.trades

import java.math.BigDecimal

data class Trade (
    val tradeId: Long,
    val timestamp: Long,
    val price: BigDecimal,
    val tradeLegs: Set<TradeLeg>
)