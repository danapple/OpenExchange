package com.danapple.openexchange.trades

import com.danapple.openexchange.orders.Order
import java.util.*

data class TradeLeg private constructor(val tradeLegId: String, val quantity: Int, val order: Order)
{
    companion object TradeLegFactory
    {
        fun createTradeLeg(quantity: Int, order: Order): TradeLeg
        {
            if (quantity <= 0)
            {
                throw IllegalArgumentException(String.format("Quantity must be positive, not %d", quantity))
            }
            return TradeLeg(UUID.randomUUID().toString(), quantity, order)
        }
    }
}
