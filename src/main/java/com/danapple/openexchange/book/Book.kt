package com.danapple.openexchange.book

import com.danapple.openexchange.orders.OrderState
import com.danapple.openexchange.orders.OrderStatus
import java.math.BigDecimal
import java.util.*

class Book {
    private val buySide = TreeMap<BigDecimal, Level>()
    private val sellSide = TreeMap<BigDecimal, Level>()

    internal fun addOrder(orderState: OrderState) {
        val book = if (orderState.order.isBuyOrder()) buySide else sellSide
        book.computeIfAbsent(orderState.order.price, { Level() }).addOrder(orderState)
    }

    internal fun cancelOrder(orderState: OrderState) {
        removeOrder(orderState, OrderStatus.CANCELED)
    }

    internal fun fillOrder(orderState: OrderState) {
        removeOrder(orderState, OrderStatus.FILLED)
    }

    private fun removeOrder(orderState: OrderState, newOrderStatus: OrderStatus) {
        if (orderState.orderStatus == OrderStatus.OPEN) {
            orderState.orderStatus = newOrderStatus
        }
        val book = if (orderState.order.isBuyOrder()) buySide else sellSide
        val removeLevel = book[orderState.order.price]?.removeOrder(orderState) ?: false
        if (removeLevel) {
            book.remove(orderState.order.price)
        }
    }

    internal fun getMatchingOppositeLevels(orderState: OrderState): SequencedCollection<Level> {
        if (orderState.order.isBuyOrder()) {
            return sellSide.headMap(orderState.order.price, true).sequencedValues()
        } else {
            return buySide.tailMap(orderState.order.price, true).sequencedValues().reversed()
        }
    }
}