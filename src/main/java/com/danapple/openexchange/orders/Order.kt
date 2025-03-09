package com.danapple.openexchange.orders

import com.danapple.openexchange.customers.Customer
import com.danapple.openexchange.instruments.Instrument
import java.math.BigDecimal

data class Order internal constructor(
    val orderId: Long,
    val customer: Customer,
    val timeStamp: Long,
    val clientOrderId: String,
    val instrument: Instrument,
    val price: BigDecimal,
    val quantity: Int
) {
    fun isBuyOrder(): Boolean {
        return quantity > 0
    }

    fun isSellOrder(): Boolean {
        return quantity < 0
    }
}
