package com.danapple.openexchange.entities.trades

import com.danapple.openexchange.orders.OrderState

data class TradeLeg (
    val tradeLegId: Long,
    val trade: Trade,
    val orderState: OrderState,
    val quantity: Int) {

    override fun toString(): String {
        return "TradeLeg(tradeLegId=$tradeLegId, trade=${trade.tradeId}, orderState=${orderState.order.orderId}, quantity=$quantity)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TradeLeg) return false

        if (tradeLegId != other.tradeLegId) return false

        return true
    }

    override fun hashCode(): Int {
        return tradeLegId.hashCode()
    }
}

