package com.danapple.openexchange.orders

import com.danapple.openexchange.dto.OrderStatus
import com.danapple.openexchange.entities.trades.TradeLeg
import java.util.*
import kotlin.math.absoluteValue

class OrderState(val order: Order, passedOrderStatus : OrderStatus = OrderStatus.OPEN, filledQty : Int = 0) {
    var orderStatus = passedOrderStatus

    private var _remainingQuantity = order.quantity - filledQty
    val tradeLegs = LinkedList<TradeLeg>()
    internal val remainingQuantity
        get() = this._remainingQuantity

    fun addTradeLeg(tradeLeg: TradeLeg) {
        if (tradeLeg.quantity.absoluteValue > _remainingQuantity.absoluteValue) {
            throw IllegalArgumentException("TradeLeg quantity ${tradeLeg.quantity} exceeds order remaining quantity $_remainingQuantity")
        }
        tradeLegs.add(tradeLeg)
        _remainingQuantity -= tradeLeg.quantity

        if (_remainingQuantity == 0) {
            orderStatus = OrderStatus.FILLED
        }
    }

    fun fill(quantity : Int) {
        _remainingQuantity -= quantity
    }

    override fun toString(): String {
        return "OrderState(order=$order, orderStatus=$orderStatus, _remainingQuantity=$_remainingQuantity, tradeLegs count=$tradeLegs.size, remainingQuantity=$remainingQuantity)"
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