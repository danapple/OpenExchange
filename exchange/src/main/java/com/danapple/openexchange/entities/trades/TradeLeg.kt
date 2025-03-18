package com.danapple.openexchange.entities.trades

import com.danapple.openexchange.orders.Order

data class TradeLeg (
    val tradeId: Long,
    val order: Order,
    val quantity: Int
)

