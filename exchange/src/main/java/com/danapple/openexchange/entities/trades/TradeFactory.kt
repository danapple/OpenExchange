package com.danapple.openexchange.entities.trades

import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class TradeFactory (private val tradeIdGenerator: TradeIdGenerator) {
    fun createTrade(createTime: Long, price: BigDecimal): Trade {
        return Trade(tradeIdGenerator.getId(), createTime, price, HashSet())
    }
}