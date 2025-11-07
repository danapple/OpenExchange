package com.danapple.openexchange.book

import com.danapple.openexchange.dto.OrderStatus
import com.danapple.openexchange.dto.PriceLevel
import com.danapple.openexchange.entities.instruments.Instrument
import com.danapple.openexchange.orders.OrderState
import java.math.BigDecimal
import java.util.*
import kotlin.math.absoluteValue

class Book(val instrument : Instrument) {
    private val buySide = TreeMap<BigDecimal, Level>()
    private val sellSide = TreeMap<BigDecimal, Level>()

    internal fun addOrder(orderState: OrderState) {
        val book = if (orderState.order.isBuyOrder()) buySide else sellSide
        book.computeIfAbsent(orderState.order.price) { Level() }.addOrder(orderState)
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
        val levelIsEmpty = book[orderState.order.price]?.removeOrder(orderState) ?: false
        if (levelIsEmpty) {
            book.remove(orderState.order.price)
        }
    }

    internal fun getMatchingOppositeLevels(orderState: OrderState): SequencedCollection<Level> {
        if (orderState.order.isBuyOrder()) {
            return sellSide.headMap(orderState.order.price, true).sequencedValues()
        } else {
            val tailMap = buySide.tailMap(orderState.order.price, true)
            return tailMap.sequencedValues().reversed()
        }
    }

    fun getBestBuyPriceLevels(numberOfLevels : Int): List<PriceLevel> {
        return buySide.descendingMap().entries.take(numberOfLevels).map{ level ->
            PriceLevel(level.key, level.value.getOrderStates().sumOf { orderState -> orderState.remainingQuantity })
        }.toList()
    }

    fun getBestSellPriceLevels(numberOfLevels : Int): List<PriceLevel> {
        return sellSide.entries.take(numberOfLevels).map{ level ->
            PriceLevel(level.key, level.value.getOrderStates().sumOf { orderState -> orderState.remainingQuantity.absoluteValue })
        }.toList()
    }
}