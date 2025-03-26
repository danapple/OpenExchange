package com.danapple.openexchange.orders

import com.danapple.openexchange.dto.OrderLeg
import com.danapple.openexchange.entities.customers.Customer
import com.danapple.openexchange.entities.instruments.Instrument
import java.math.BigDecimal

data class Order internal constructor(
    val orderId: Long,
    val customer: Customer,
    val createTime: Long,
    val clientOrderId: String,
    val instrument: Instrument,
    val price: BigDecimal,
    val quantity: Int) {
    fun isBuyOrder(): Boolean {
        return quantity > 0
    }

    fun isSellOrder(): Boolean {
        return quantity < 0
    }

    fun toDtoOrder() : com.danapple.openexchange.dto.Order {
        return com.danapple.openexchange.dto.Order(
            clientOrderId,
            price,
            quantity,
            listOf(OrderLeg(instrument.instrumentId, 1))
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Order) return false

        if (orderId != other.orderId) return false

        return true
    }

    override fun hashCode(): Int {
        return orderId.hashCode()
    }
}
