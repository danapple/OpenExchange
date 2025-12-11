package com.danapple.openexchange.engine

import com.danapple.openexchange.UnitTest
import com.danapple.openexchange.book.Book
import com.danapple.openexchange.customerupdates.CustomerUpdateSender
import com.danapple.openexchange.dao.TradeDao
import com.danapple.openexchange.dto.OrderStatus
import com.danapple.openexchange.entities.trades.Trade
import com.danapple.openexchange.marketdata.MarketDataPublisher
import com.danapple.openexchange.orders.OrderState
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Clock

class EngineTest : UnitTest() {
    private val orderStateBuy1 = OrderState(ORDER_BUY_1, ORDER_CREATETIME_1)
    private val orderStateBuy2 = OrderState(ORDER_BUY_2, ORDER_CREATETIME_2)

    private val orderStateSell1 = OrderState(ORDER_SELL_1, ORDER_CREATETIME_1)
    private val orderStateSell2 = OrderState(ORDER_SELL_2, ORDER_CREATETIME_2)

    private val orderBuy1Big = orderFactory.createOrder(
        CUSTOMER,
        ORDER_CREATETIME_2,
        CL_ORD_BUY_1,
        INSTRUMENT_1,
        BigDecimal.ONE,
        ORDER_QUANTITY_1 + 3
    )
    private val orderStateBuy1Big = OrderState(orderBuy1Big, ORDER_CREATETIME_1)

    private val orderSell1Big = orderFactory.createOrder(
        CUSTOMER,
        ORDER_CREATETIME_2,
        CL_ORD_SELL_1,
        INSTRUMENT_1,
        BigDecimal.ONE,
        -ORDER_QUANTITY_1 - 7
    )
    private val orderStateSell1Big = OrderState(orderSell1Big, ORDER_CREATETIME_1)

    private var tradeDao = mockk<TradeDao>()

    private val book = Book(INSTRUMENT_1)
    private val marketDataPublisher = mockk<MarketDataPublisher>()

    private val customerUpdateSender = mockk<CustomerUpdateSender>()

    private val engine = Engine(
        book,
        Clock.systemDefaultZone(),
        tradeFactory,
        tradeLegFactory,
        orderDao,
        tradeDao,
        marketDataPublisher,
        customerUpdateSender
    )

    private val tradeSlot = slot<Trade>()

    @BeforeEach
    fun beforeEach() {
        every { tradeDao.saveTrade(capture(tradeSlot)) } just runs
        every { marketDataPublisher.publishTopOfBook(any(), any()) } just runs
        every { marketDataPublisher.publishTrades(any(), any()) } just runs
        every { customerUpdateSender.sendOrderState(any()) } just runs
        every { customerUpdateSender.sendTrade(any()) } just runs
    }

    @Test
    fun addsNewUnmatchedOrderToBook() {
        engine.newOrder(orderStateBuy2)

        val matchingOppositeLevels = book.getMatchingOppositeLevels(orderStateSell2)
        assertThat(matchingOppositeLevels).isNotEmpty()
        assertThat(matchingOppositeLevels.first.getOrderStates()).containsExactly(orderStateBuy2)
    }

    @Test
    fun removesCanceledOrderFromBook() {
        engine.newOrder(orderStateBuy2)
        engine.cancelOrder(orderStateBuy2)

        val matchingOppositeLevels = book.getMatchingOppositeLevels(orderStateSell2)
        assertThat(matchingOppositeLevels).isEmpty()
    }

    @Test
    fun removesFilledOrderFromBook() {
        engine.newOrder(orderStateBuy1)
        engine.newOrder(orderStateSell1)

        assertThat(book.getMatchingOppositeLevels(orderStateSell1)).isEmpty()
    }

    @Test
    fun doesNotSaveFilledNewOrderToBook() {
        engine.newOrder(orderStateBuy1)
        engine.newOrder(orderStateSell1)

        assertThat(book.getMatchingOppositeLevels(orderStateBuy1)).isEmpty()
    }

    @Test
    fun cancelsOrder() {
        engine.newOrder(orderStateBuy2)
        engine.cancelOrder(orderStateBuy2)

        assertThat(orderStateBuy2.orderStatus).isEqualTo(OrderStatus.CANCELED)
    }

    @Test
    fun savesNewUnmatched() {
        engine.newOrder(orderStateBuy2)

        verify { orderDao.saveOrder(orderStateBuy2) }
    }

    @Test
    fun updatesCanceledOrder() {
        engine.newOrder(orderStateBuy2)
        engine.cancelOrder(orderStateBuy2)

        verify { orderDao.updateOrder(orderStateBuy2) }
    }

    @Test
    fun updatesFilledRestingOrder() {
        engine.newOrder(orderStateBuy1)
        engine.newOrder(orderStateSell1)

        verify { orderDao.updateOrder(orderStateBuy1) }
    }

    @Test
    fun updatesFilledNewOrder() {
        engine.newOrder(orderStateBuy1)
        engine.newOrder(orderStateSell1)

        verify { orderDao.updateOrder(orderStateSell1) }
    }

    @Test
    fun savesTradeForFill() {
        engine.newOrder(orderStateBuy1)
        engine.newOrder(orderStateSell1)

        val capturedTrade = tradeSlot.captured
        assertThat(capturedTrade.tradeLegs).hasSize(2)
        val tradeLegsByOrderState = capturedTrade.tradeLegs.associateBy { it.orderState }

        assertThat(tradeLegsByOrderState[orderStateBuy1]?.quantity).isEqualTo(orderStateBuy1.filledQuantity())
        assertThat(tradeLegsByOrderState[orderStateSell1]?.quantity).isEqualTo(orderStateSell1.filledQuantity())
    }

    @Test
    fun fillsNewAndMatchedRestingOrder() {
        engine.newOrder(orderStateBuy2)
        engine.newOrder(orderStateSell2)

        assertThat(orderStateBuy2.orderStatus).isEqualTo(OrderStatus.FILLED)
        assertThat(orderStateSell2.orderStatus).isEqualTo(OrderStatus.FILLED)

        assertThat(orderStateBuy2.remainingQuantity).isEqualTo(0)
        assertThat(orderStateSell2.remainingQuantity).isEqualTo(0)

        assertThat(orderStateBuy2.tradeLegs).hasSize(1)
        assertThat(orderStateSell2.tradeLegs).hasSize(1)

        assertThat(orderStateBuy2.tradeLegs.first.orderState).isEqualTo(orderStateBuy2)
        assertThat(orderStateSell2.tradeLegs.first.orderState).isEqualTo(orderStateSell2)

        assertThat(orderStateBuy2.tradeLegs.first.quantity).isEqualTo(orderStateBuy2.order.quantity)
        assertThat(orderStateSell2.tradeLegs.first.quantity).isEqualTo(orderStateSell2.order.quantity)
    }

    @Test
    fun doesNotMatchCanceledOrder() {
        engine.newOrder(orderStateBuy2)
        engine.cancelOrder(orderStateBuy2)
        engine.newOrder(orderStateSell2)

        assertThat(orderStateBuy2.orderStatus).isEqualTo(OrderStatus.CANCELED)
        assertThat(orderStateSell2.orderStatus).isEqualTo(OrderStatus.OPEN)

        assertThat(orderStateBuy2.remainingQuantity).isEqualTo(orderStateBuy2.order.quantity)
        assertThat(orderStateSell2.remainingQuantity).isEqualTo(orderStateSell2.order.quantity)

        assertThat(orderStateBuy2.tradeLegs).isEmpty()
        assertThat(orderStateSell2.tradeLegs).isEmpty()
    }

    @Test
    fun partiallyMatchesBigNewOrder() {
        engine.newOrder(orderStateBuy1)
        engine.newOrder(orderStateSell1Big)

        assertThat(orderStateBuy1.orderStatus).isEqualTo(OrderStatus.FILLED)
        assertThat(orderStateSell1Big.orderStatus).isEqualTo(OrderStatus.OPEN)

        assertThat(orderStateBuy1.remainingQuantity).isEqualTo(0)
        assertThat(orderStateSell1Big.remainingQuantity).isEqualTo(orderSell1Big.quantity + orderStateBuy1.order.quantity)

        assertThat(orderStateBuy1.tradeLegs).hasSize(1)
        assertThat(orderStateSell1Big.tradeLegs).hasSize(1)

        assertThat(orderStateBuy1.tradeLegs.first.orderState).isEqualTo(orderStateBuy1)
        assertThat(orderStateSell1Big.tradeLegs.first.orderState).isEqualTo(orderStateSell1Big)

        assertThat(orderStateBuy1.tradeLegs.first.quantity).isEqualTo(orderStateBuy1.order.quantity)
        assertThat(orderStateSell1Big.tradeLegs.first.quantity).isEqualTo(-orderStateBuy1.order.quantity)
    }

    @Test
    fun partiallyMatchesBigRestingOrder() {
        engine.newOrder(orderStateSell1Big)
        engine.newOrder(orderStateBuy1)

        assertThat(orderStateSell1Big.orderStatus).isEqualTo(OrderStatus.OPEN)
        assertThat(orderStateBuy1.orderStatus).isEqualTo(OrderStatus.FILLED)

        assertThat(orderStateSell1Big.remainingQuantity).isEqualTo(orderSell1Big.quantity + orderStateBuy1.order.quantity)
        assertThat(orderStateBuy1.remainingQuantity).isEqualTo(0)

        assertThat(orderStateSell1Big.tradeLegs).hasSize(1)
        assertThat(orderStateBuy1.tradeLegs).hasSize(1)

        assertThat(orderStateSell1Big.tradeLegs.first.orderState).isEqualTo(orderStateSell1Big)
        assertThat(orderStateBuy1.tradeLegs.first.orderState).isEqualTo(orderStateBuy1)

        assertThat(orderStateSell1Big.tradeLegs.first.quantity).isEqualTo(-orderStateBuy1.order.quantity)
        assertThat(orderStateBuy1.tradeLegs.first.quantity).isEqualTo(orderStateBuy1.order.quantity)
    }

    @Test
    fun fillsRemainderOfBigRestingOrder() {
        engine.newOrder(orderStateSell1Big)
        engine.newOrder(orderStateBuy1)
        engine.newOrder(orderStateBuy1Big)

        assertThat(orderStateSell1Big.orderStatus).isEqualTo(OrderStatus.FILLED)
        assertThat(orderStateBuy1.orderStatus).isEqualTo(OrderStatus.FILLED)
        assertThat(orderStateBuy1Big.orderStatus).isEqualTo(OrderStatus.OPEN)

        assertThat(orderStateSell1Big.remainingQuantity).isEqualTo(0)
        assertThat(orderStateBuy1.remainingQuantity).isEqualTo(0)

        val order1BigRemainingQuantity =
            orderBuy1Big.quantity + (orderSell1Big.quantity + orderStateBuy1.order.quantity)
        assertThat(orderStateBuy1Big.remainingQuantity).isEqualTo(order1BigRemainingQuantity)

        assertThat(orderStateSell1Big.tradeLegs).hasSize(2)
        assertThat(orderStateBuy1.tradeLegs).hasSize(1)
        assertThat(orderStateBuy1Big.tradeLegs).hasSize(1)

        assertThat(orderStateSell1Big.tradeLegs[0].orderState).isEqualTo(orderStateSell1Big)
        assertThat(orderStateSell1Big.tradeLegs[1].orderState).isEqualTo(orderStateSell1Big)
        assertThat(orderStateBuy1.tradeLegs.first.orderState).isEqualTo(orderStateBuy1)
        assertThat(orderStateBuy1Big.tradeLegs.first.orderState).isEqualTo(orderStateBuy1Big)

        assertThat(orderStateSell1Big.tradeLegs[0].quantity).isEqualTo(-orderStateBuy1.order.quantity)
        assertThat(orderStateSell1Big.tradeLegs[1].quantity).isEqualTo(-orderStateBuy1Big.tradeLegs.first.quantity)
        assertThat(orderStateBuy1.tradeLegs.first.quantity).isEqualTo(orderStateBuy1.order.quantity)
        assertThat(orderStateBuy1Big.tradeLegs.first.quantity).isEqualTo(orderStateBuy1Big.order.quantity - order1BigRemainingQuantity)
    }

    @Test
    fun cancelsRestingOrder() {
        engine.newOrder(orderStateBuy2)
        engine.cancelReplace(orderStateBuy2, orderStateBuy1)

        assertThat(orderStateBuy2.orderStatus).isEqualTo(OrderStatus.CANCELED)
        assertThat(book.getMatchingOppositeLevels(orderStateSell2)).isEmpty()
    }

    @Test
    fun insertsReplacementOrderInBook() {
        engine.newOrder(orderStateBuy2)
        engine.cancelReplace(orderStateBuy2, orderStateBuy1)

        val matchingOppositeLevels = book.getMatchingOppositeLevels(orderStateSell1)
        assertThat(matchingOppositeLevels).hasSize(1)
        assertThat(matchingOppositeLevels.first.getOrderStates()).containsExactly(orderStateBuy1)
    }

    @Test
    fun CancelReplaceDoesNotCancelFilledOrder() {
        engine.newOrder(orderStateBuy2)
        engine.newOrder(orderStateSell2)

        engine.cancelReplace(orderStateBuy2, orderStateBuy1)
        assertThat(orderStateBuy2.orderStatus).isEqualTo(OrderStatus.FILLED)
    }
}