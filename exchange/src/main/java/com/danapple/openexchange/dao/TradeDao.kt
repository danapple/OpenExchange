package com.danapple.openexchange.dao

import com.danapple.openexchange.entities.trades.Trade

interface TradeDao {
    fun saveTrade(trade: Trade)
}