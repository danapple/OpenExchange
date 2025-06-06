package com.danapple.openexchange.engine

import com.danapple.openexchange.book.Book
import com.danapple.openexchange.dao.OrderDao
import com.danapple.openexchange.dao.TradeDao
import com.danapple.openexchange.dto.OrderStatus
import com.danapple.openexchange.entities.trades.Trade
import com.danapple.openexchange.entities.trades.TradeFactory
import com.danapple.openexchange.entities.trades.TradeLegFactory
import com.danapple.openexchange.marketdata.MarketDataPublisher
import com.danapple.openexchange.orders.OrderState
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Clock
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.sign

class Engine(private val book: Book, private val clock : Clock, private val tradeFactory: TradeFactory,
             private val tradeLegFactory : TradeLegFactory, private val orderDao: OrderDao, private val tradeDao : TradeDao,
             private val marketDataPublisher : MarketDataPublisher)
{
    @Synchronized internal fun newOrder(orderState: OrderState) {
        val timestamp = clock.millis()
        orderDao.saveOrder(orderState)
        val matchingOppositeLevels = book.getMatchingOppositeLevels(orderState)
        logger.info("matchingOppositeLevels = $matchingOppositeLevels")
        val trades = LinkedHashSet<Trade>()
        run fullyMatched@
        {
            matchingOppositeLevels.forEach { level ->
                val trade = tradeFactory.createTrade(timestamp, orderState.order.price)

                level.getOrderStates().forEach { oppositeOrderState ->
                    matchOrderStates(orderState, oppositeOrderState, trade, trades)
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
        marketDataPublisher.publishTopOfBook(timestamp, book)
        trades.forEach { trade ->
            tradeDao.saveTrade(trade)
            marketDataPublisher.publishTrades(timestamp, trade)
        }
        if (orderState.remainingQuantity != 0) {
            logger.info("Order $orderState has remaining quantity ${orderState.remainingQuantity}, adding to book")
            book.addOrder(orderState)
        }
    }

    @Synchronized internal fun cancelOrder(orderState: OrderState) {
        if (orderState.orderStatus.viability == OrderStatus.Viability.ALIVE) {
            book.cancelOrder(orderState)
            orderDao.updateOrder(orderState)
        }
        marketDataPublisher.publishTopOfBook(clock.millis(), book)
    }

    @Synchronized internal fun cancelReplace(originalOrderState: OrderState, newOrderState: OrderState) {
        cancelOrder(originalOrderState)
        newOrder(newOrderState)
        marketDataPublisher.publishTopOfBook(clock.millis(), book)
    }

    private fun matchOrderStates(orderState: OrderState, oppositeOrderState: OrderState, trade: Trade, trades: SequencedSet<Trade>) {
        val matchQuantity = min(orderState.remainingQuantity.absoluteValue, oppositeOrderState.remainingQuantity.absoluteValue)
        if (matchQuantity != 0) {
            tradeLegFactory.createTradeLeg(orderState, trade, matchQuantity * orderState.order.quantity.sign)
            tradeLegFactory.createTradeLeg(oppositeOrderState, trade, matchQuantity * oppositeOrderState.order.quantity.sign)
            orderDao.updateOrder(orderState)
            orderDao.updateOrder(oppositeOrderState)
            trades.add(trade)
        }
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(Engine::class.java)
    }
}