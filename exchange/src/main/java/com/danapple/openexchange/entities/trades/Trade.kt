package com.danapple.openexchange.entities.trades

import java.math.BigDecimal

data class Trade(
    val tradeId: Long,
    val createTime: Long,
    val price: BigDecimal,
    val tradeLegs: MutableSet<TradeLeg>
) {

    fun addTradeLeg(tradeLeg: TradeLeg) {
        tradeLegs.add(tradeLeg)
    }

    override fun toString(): String {
        return "Trade(tradeId=$tradeId, createTime=$createTime, price=$price, tradeLegs count=${tradeLegs.size})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Trade) return false

        if (tradeId != other.tradeId) return false

        return true
    }

    override fun hashCode(): Int {
        return tradeId.hashCode()
    }
}