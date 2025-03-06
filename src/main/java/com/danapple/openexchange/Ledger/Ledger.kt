package com.danapple.openexchange.Ledger

import com.danapple.openexchange.trades.Trade
import java.util.*

class Ledger
{
    private val trades: MutableList<Trade> = LinkedList()

    internal fun addTrade(trade: Trade)
    {
        trades.add(trade)
    }

    fun getTrades(): List<Trade>
    {
        return trades
    }
}