package com.danapple.openexchange.engine

import com.danapple.openexchange.book.Book
import com.danapple.openexchange.customerupdates.CustomerUpdateSender
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

class Engine(
    private val book: Book, private val clock: Clock, private val tradeFactory: TradeFactory,
    private val tradeLegFactory: TradeLegFactory, private val orderDao: OrderDao, private val tradeDao: TradeDao,
    private val marketDataPublisher: MarketDataPublisher,
    private val customerUpdateSender: CustomerUpdateSender
)
{
    @Synchronized internal fun publishInitialTopOfBook() {
        marketDataPublisher.publishTopOfBook(clock.millis(), book)
    }

    @Synchronized internal fun newOrder(orderState: OrderState) {
        val timestamp = clock.millis()
        orderDao.saveOrder(orderState)
        val matchingOppositeLevels = book.getMatchingOppositeLevels(orderState)
        if (logger.isDebugEnabled) {
            logger.debug("matchingOppositeLevels = $matchingOppositeLevels")
        }
        val trades = LinkedHashSet<Trade>()
        run fullyMatched@
        {
            matchingOppositeLevels.forEach { level ->
                val trade = tradeFactory.createTrade(timestamp, orderState.order.price)

                level.getOrderStates().forEach { oppositeOrderState ->
                    matchOrderStates(orderState, oppositeOrderState, trade, trades)
                    if (orderState.remainingQuantity == 0)
                    {
                        return@fullyMatched
                    }
                }
            }
        }
        trades.forEach { trade ->
            tradeDao.saveTrade(trade)
            marketDataPublisher.publishTrades(timestamp, trade)
            customerUpdateSender.sendTrade(trade)
        }
        if (orderState.remainingQuantity != 0) {
            if (logger.isDebugEnabled) {
                logger.debug("Order $orderState has remaining quantity ${orderState.remainingQuantity}, adding to book")
            }
            book.addOrder(orderState)
        }
        else {
            orderState.orderStatus = OrderStatus.FILLED
            orderDao.updateOrder(orderState)
        }
        customerUpdateSender.sendOrderState(orderState)
        marketDataPublisher.publishTopOfBook(timestamp, book)
    }

    @Synchronized internal fun cancelOrder(orderState: OrderState) {
        if (orderState.orderStatus.viability == OrderStatus.Viability.ALIVE) {
            book.cancelOrder(orderState)
            orderDao.updateOrder(orderState)
            customerUpdateSender.sendOrderState(orderState)
            marketDataPublisher.publishTopOfBook(clock.millis(), book)
        }
    }

    @Synchronized internal fun cancelReplace(originalOrderState: OrderState, newOrderState: OrderState) {
        cancelOrder(originalOrderState)
        newOrder(newOrderState)
    }

    private fun matchOrderStates(orderState: OrderState, oppositeOrderState: OrderState, trade: Trade, trades: SequencedSet<Trade>) {
        val matchQuantity = min(orderState.remainingQuantity.absoluteValue, oppositeOrderState.remainingQuantity.absoluteValue)
        if (matchQuantity != 0) {
            tradeLegFactory.createTradeLeg(orderState, trade, matchQuantity * orderState.order.quantity.sign)
            tradeLegFactory.createTradeLeg(oppositeOrderState, trade, matchQuantity * oppositeOrderState.order.quantity.sign)
            orderDao.updateOrder(orderState)
            customerUpdateSender.sendOrderState(orderState)

            if (oppositeOrderState.remainingQuantity == 0)
            {
                if (logger.isDebugEnabled) {
                    logger.debug("oppositeOrderState $oppositeOrderState is filled, removing from book")
                }
                book.fillOrder(oppositeOrderState)
            }
            orderDao.updateOrder(oppositeOrderState)
            customerUpdateSender.sendOrderState(oppositeOrderState)
            trades.add(trade)
        }
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(Engine::class.java)
    }
}