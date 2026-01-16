package com.danapple.openexchange.orders

import com.danapple.openexchange.dto.OrderStatus
import com.danapple.openexchange.entities.trades.TradeLeg
import java.util.*
import kotlin.math.absoluteValue

class OrderState(
    val order: Order,
    private val updateTime: Long,
    var orderStatus: OrderStatus = OrderStatus.OPEN,
    private var filledQty: Int = 0,
    var versionNumber: Int = 0
) {
    val tradeLegs = LinkedList<TradeLeg>()

    fun addTradeLeg(tradeLeg: TradeLeg) {
        if (tradeLeg.quantity.absoluteValue > remainingQuantity.absoluteValue) {
            throw IllegalArgumentException("TradeLeg quantity ${tradeLeg.quantity} exceeds order remaining quantity $remainingQuantity")
        }
        tradeLegs.add(tradeLeg)
        filledQty += tradeLeg.quantity

        if (remainingQuantity == 0) {
            orderStatus = OrderStatus.FILLED
        }
    }

    val remainingQuantity: Int
        get() = order.quantity - filledQuantity

    val filledQuantity: Int
        get() = filledQty

    override fun toString(): String {
        return "OrderState(order=$order, orderStatus=$orderStatus, updateTime=${updateTime}, _remainingQuantity=$remainingQuantity, tradeLegs count=$tradeLegs.size, remainingQuantity=$remainingQuantity)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrderState) return false

        if (order != other.order) return false

        return true
    }

    override fun hashCode(): Int {
        return order.hashCode()
    }
}