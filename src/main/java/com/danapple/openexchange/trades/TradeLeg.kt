package com.danapple.openexchange.trades

import com.danapple.openexchange.orders.Order

data class TradeLeg private constructor(val tradeLegId: Long, val quantity: Int, val order: Order)
{
    companion object TradeLegFactory
    {
        private var nextTradeLegId: Long = 0
        fun createTradeLeg(quantity: Int, order: Order): TradeLeg
        {
            if (quantity <= 0)
            {
                throw IllegalArgumentException(String.format("Quantity must be positive, not %d", quantity))
            }
            return TradeLeg(nextTradeLegId++, quantity, order)
        }
    }
}
