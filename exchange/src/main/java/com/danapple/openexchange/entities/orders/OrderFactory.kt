package com.danapple.openexchange.orders

import com.danapple.openexchange.entities.customers.Customer
import com.danapple.openexchange.dao.CustomerDao
import com.danapple.openexchange.dao.InstrumentDao
import com.danapple.openexchange.dto.CancelReplace
import com.danapple.openexchange.entities.instruments.Instrument
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.Exception
import java.math.BigDecimal
import java.time.Clock

@Service
class OrderFactory (@Autowired val clock : Clock, @Autowired val orderIdGenerator: OrderIdGenerator, @Autowired val instrumentDao: InstrumentDao, @Autowired val customerDao: CustomerDao) {

    fun createOrder(customer: Customer, timestamp: Long, clientOrderId: String, instrument: Instrument, price: BigDecimal, quantity: Int): Order {
        if (quantity == 0) {
            throw IllegalArgumentException("Quantity must be non-zero for order $clientOrderId")
        }
        if (price <= BigDecimal.ZERO) {
            throw IllegalArgumentException("Price must be positive for order $clientOrderId, not $price")
        }
        return Order(orderIdGenerator.getId(), customer, timestamp, clientOrderId, instrument, price, quantity)
    }

    fun createOrder(customer: Customer, clientOrderId: String, newOrder: com.danapple.openexchange.dto.Order) : Order {
        if (newOrder.legs.size != 1) {
            throw IllegalArgumentException("Order must have exactly one leg, not $newOrder.legs.size legs")
        }
        val leg0 = newOrder.legs[0];
        if (leg0.ratio != 1) {
            throw IllegalArgumentException("Order leg, must have a ratio of 1 not $leg0.ratio")
        }
        return createOrder(customer , clock.millis(), clientOrderId, instrumentDao.getInstrument(leg0.instrumentId), newOrder.price, newOrder.quantity * leg0.ratio)
    }

    fun createOrder(customer: Customer, clientOrderId: String, cancelReplace: CancelReplace) : Order {
        if (cancelReplace.order.legs.size != 1) {
            throw IllegalArgumentException("Order must have exactly one leg, not $cancelReplace.legs.size legs")
        }
        val leg0 = cancelReplace.order.legs[0]
        if (leg0.ratio != 1) {
            throw IllegalArgumentException("Order leg, must have a ratio of 1 not $leg0.ratio")
        }
        return createOrder(customer, clock.millis(), clientOrderId, instrumentDao.getInstrument(leg0.instrumentId), cancelReplace.order.price, cancelReplace.order.quantity * leg0.ratio)
    }
}