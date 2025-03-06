package com.danapple.openexchange.orders

import java.math.BigDecimal
import java.util.*

data class Order private constructor(val orderId: String, val clientOrderId: String, val symbol: String, val side: Side, val quantity: Int, val price: BigDecimal)
{
    companion object OrderFactory
    {
        fun createOrder(clientOrderId: String, symbol: String, side: Side, quantity: Int, price: BigDecimal): Order
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
            return Order(UUID.randomUUID().toString(), clientOrderId, symbol, side, quantity, price )
        }
    }
}
