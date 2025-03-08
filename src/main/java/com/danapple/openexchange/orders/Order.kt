package com.danapple.openexchange.orders

import com.danapple.openexchange.instruments.Instrument
import java.math.BigDecimal
import java.time.Clock

data class Order internal constructor(
    val orderId: Long,
    val customerId: Long,
    val timeStamp: Long,
    val clientOrderId: String,
    val instrument: Instrument,
    val price: BigDecimal,
    val quantity: Int
) {
    companion object OrderFactory {
        var clock: Clock = Clock.systemDefaultZone()
        fun createOrder(orderId: Long, customerId: Long, clientOrderId: String, instrument: Instrument, price: BigDecimal, quantity: Int): Order {
            if (quantity == 0) {
                throw IllegalArgumentException(String.format("Quantity must be non-zero for order %s", clientOrderId))
            }
            if (price <= BigDecimal.ZERO) {
                throw IllegalArgumentException(String.format("Price must be positive for order %s", clientOrderId))
            }
            return Order(orderId, customerId, clock.millis(), clientOrderId, instrument, price, quantity)
        }
    }

    fun isBuyOrder(): Boolean {
        return quantity > 0
    }

    fun isSellOrder(): Boolean {
        return quantity < 0
    }
}
