package com.danapple.openexchange.orders

import com.danapple.openexchange.trades.TradeLeg
import java.util.*
import kotlin.math.absoluteValue

class OrderState(val order: Order, passedOrderStatus : OrderStatus = OrderStatus.OPEN) {
    var orderStatus = passedOrderStatus

    private var _remainingQuantity = order.quantity
    private val tradeLegs = LinkedList<TradeLeg>()
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
}