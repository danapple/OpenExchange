package com.danapple.openexchange.orders

import java.math.BigDecimal

data class Order private constructor(val orderId: Long, val symbol: String, val side: Side, val quantity: Int, val price: BigDecimal)
{
    companion object OrderFactory
    {
        private var nextOrderId: Long = 0

        fun createOrder(symbol: String, side: Side, quantity: Int, price: BigDecimal): Order
        {
            if (quantity <= 0)
            {
                throw IllegalArgumentException(String.format("Quantity must be positive, not %d", quantity))
            }
            if (price <= BigDecimal.ZERO)
            {
                throw IllegalArgumentException(String.format("Price must be positive, not %f", price))
            }
            if (symbol.isBlank())
            {
                throw IllegalArgumentException("Symbol must not be blank")
            }
            return Order(nextOrderId++, symbol, side, quantity, price )
        }
    }
}
