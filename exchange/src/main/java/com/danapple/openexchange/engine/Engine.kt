package com.danapple.openexchange.engine

import com.danapple.openexchange.book.Book
import com.danapple.openexchange.orders.OrderState
import com.danapple.openexchange.entities.trades.TradeFactory
import com.danapple.openexchange.entities.trades.TradeLeg
import com.danapple.openexchange.entities.trades.TradeLegFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Clock
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.sign

class Engine(private val book: Book, private val clock : Clock, private val tradeFactory: TradeFactory, private val tradeLegFactory : TradeLegFactory)
{
    @Synchronized internal fun newOrder(orderState: OrderState)
    {
        val timestamp = clock.millis()
        val matchingOppositeLevels = book.getMatchingOppositeLevels(orderState)
        logger.info("matchingOppositeLevels = $matchingOppositeLevels")
        val tradeLegs: MutableSet<TradeLeg> = HashSet()
        run fullyMatched@
        {
            matchingOppositeLevels.forEach { level ->
                level.getOrderStates().forEach { oppositeOrderState ->
                    matchOrderStates(orderState, oppositeOrderState, tradeLegs)
                    if (oppositeOrderState.remainingQuantity == 0)
                    {
                        logger.info("oppositeOrderState $oppositeOrderState is filled, removing from book")
                        book.fillOrder(oppositeOrderState)
                    }
                    if (orderState.remainingQuantity == 0)
                    {
                        return@fullyMatched
                    }
                }
            }
        }
        if (orderState.remainingQuantity != 0) {
            logger.info("Order $orderState has remaining quantity ${orderState.remainingQuantity}, adding to book")
            book.addOrder(orderState)
        }
        if (tradeLegs.size > 0) {
            val trade = tradeFactory.createTrade(timestamp, orderState.order.price, tradeLegs)
            logger.info("Created trade $trade")
        }
    }

    @Synchronized internal fun cancelOrder(orderState: OrderState) {
        book.cancelOrder(orderState)
    }

    @Synchronized internal fun cancelReplace(originalOrderState: OrderState, newOrderState: OrderState) {
        cancelOrder(originalOrderState)
        newOrder(newOrderState)
    }

    private fun matchOrderStates(
        orderState: OrderState,
        oppositeOrderState: OrderState,
        tradeLegs: MutableSet<TradeLeg>
    )
    {
        val matchQuantity = min(orderState.remainingQuantity.absoluteValue, oppositeOrderState.remainingQuantity.absoluteValue)
        val tradeLeg = tradeLegFactory.createTradeLeg(orderState.order, matchQuantity * orderState.order.quantity.sign)
        val oppositeTradeLeg = tradeLegFactory.createTradeLeg(oppositeOrderState.order, matchQuantity * oppositeOrderState.order.quantity.sign)
        tradeLegs.add(tradeLeg)
        orderState.addTradeLeg(tradeLeg)
        tradeLegs.add(oppositeTradeLeg)
        oppositeOrderState.addTradeLeg(oppositeTradeLeg)
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(Engine::class.java)
    }
}