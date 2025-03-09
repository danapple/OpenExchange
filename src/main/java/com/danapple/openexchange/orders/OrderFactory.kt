package com.danapple.openexchange.orders

import com.danapple.openexchange.customers.Customer
import com.danapple.openexchange.instruments.Instrument
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class OrderFactory (private val orderIdGenerator: OrderIdGenerator) {

    fun createOrder(customer: Customer, timestamp: Long, clientOrderId: String, instrument: Instrument, price: BigDecimal, quantity: Int): Order {
        if (quantity == 0) {
            throw IllegalArgumentException("Quantity must be non-zero for order $clientOrderId")
        }
        if (price <= BigDecimal.ZERO) {
            throw IllegalArgumentException("Price must be positive for order $clientOrderId, not $price")
        }
        return Order(orderIdGenerator.getId(), customer, timestamp, clientOrderId, instrument, price, quantity)
    }
//
//    fun com.danapple.openexchange.dto.Order.createOrder(customerId: Long, clientOrderId: String, timestamp: Long) : Order {
//        if (legs.size != 1) {
//            throw IllegalArgumentException("Order must have exactly one leg, not $legs.size legs")
//        }
//        val leg0 = legs[0];
//        if (leg0.ratio != 1) {
//            throw IllegalArgumentException("Order leg, must have a ratio of 1 not $leg0.ratio")
//        }
//        return createOrder(customerId, timestamp, clientOrderId, leg0.instrumentId, this.price, this.quantity * leg0.ratio)
//    }
}