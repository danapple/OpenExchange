package com.danapple.openexchange.orders

import com.danapple.openexchange.instruments.Instrument
import java.math.BigDecimal
import java.time.Clock
import java.util.*

data class Order private constructor(
    val orderId: String,
    val timeStamp: Long,
    val clientOrderId: String,
    val instrument: Instrument,
    val price: BigDecimal,
    val quantity: Int
) {
    companion object OrderFactory {
        var clock: Clock = Clock.systemDefaultZone()

        fun createOrder(clientOrderId: String, instrument: Instrument, price: BigDecimal, quantity: Int): Order {
            if (quantity == 0) {
                throw IllegalArgumentException(String.format("Quantity must be non-zero for order %s", clientOrderId))
            }
            if (price <= BigDecimal.ZERO) {
                throw IllegalArgumentException(String.format("Price must be positive for order %s", clientOrderId))
            }
            val orderId = UUID.randomUUID().toString()
            return Order(orderId, clock.millis(), clientOrderId, instrument, price, quantity)
        }
    }

    fun isBuyOrder(): Boolean {
        return quantity > 0
    }

    fun isSellOrder(): Boolean {
        return quantity < 0
    }
}
