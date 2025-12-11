package com.danapple.openexchange.entities.trades

import com.danapple.openexchange.dao.IdGenerator
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class TradeFactory(@Qualifier("tradeIdGenerator") private val tradeIdGenerator: IdGenerator) {
    fun createTrade(createTime: Long, price: BigDecimal): Trade {
        return Trade(tradeIdGenerator.getNextId(), createTime, price, HashSet())
    }
}