package com.danapple.openexchange.engine

import com.danapple.openexchange.TestConstants.Companion.CL_ORD_BUY_1
import com.danapple.openexchange.TestConstants.Companion.CL_ORD_SELL_1
import com.danapple.openexchange.TestConstants.Companion.CUSTOMER
import com.danapple.openexchange.TestConstants.Companion.EQUITY_1
import com.danapple.openexchange.TestConstants.Companion.ORDER_BUY_1
import com.danapple.openexchange.TestConstants.Companion.ORDER_BUY_2
import com.danapple.openexchange.TestConstants.Companion.ORDER_FACTORY
import com.danapple.openexchange.TestConstants.Companion.ORDER_QUANTITY_1
import com.danapple.openexchange.TestConstants.Companion.ORDER_SELL_1
import com.danapple.openexchange.TestConstants.Companion.ORDER_SELL_2
import com.danapple.openexchange.TestConstants.Companion.ORDER_TIMESTAMP_2
import com.danapple.openexchange.TestConstants.Companion.TRADE_FACTORY
import com.danapple.openexchange.TestConstants.Companion.TRADE_LEG_FACTORY
import com.danapple.openexchange.book.Book
import com.danapple.openexchange.dao.OrderDao
import com.danapple.openexchange.dto.OrderStatus
import com.danapple.openexchange.orders.OrderState
import io.mockk.*
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.Clock

@ExtendWith(MockKExtension::class)
class EngineTest {
    private val orderStateBuy1 = OrderState(ORDER_BUY_1)
    private val orderStateBuy2 = OrderState(ORDER_BUY_2)

    private val orderStateSell1 = OrderState(ORDER_SELL_1)
    private val orderStateSell2 = OrderState(ORDER_SELL_2)

    private val orderBuy1Big = ORDER_FACTORY.createOrder(CUSTOMER, ORDER_TIMESTAMP_2, CL_ORD_BUY_1, EQUITY_1, BigDecimal.ONE, ORDER_QUANTITY_1 + 3)
    private val orderStateBuy1Big = OrderState(orderBuy1Big)

    private val orderSell1Big = ORDER_FACTORY.createOrder(CUSTOMER, ORDER_TIMESTAMP_2, CL_ORD_SELL_1, EQUITY_1, BigDecimal.ONE, -ORDER_QUANTITY_1 - 7)
    private val orderStateSell1Big = OrderState(orderSell1Big)

    private var orderDao = mockk<OrderDao>()

    private val book = Book()

    private val engine = Engine(book, Clock.systemDefaultZone(), TRADE_FACTORY, TRADE_LEG_FACTORY, orderDao)

    @BeforeEach
    fun beforeEach() {
        every { orderDao.saveOrder(any())} just runs
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
    fun fillsNewAndMatchedRestingOrder() {
        engine.newOrder(orderStateBuy2)
        engine.newOrder(orderStateSell2)

        assertThat(orderStateBuy2.orderStatus).isEqualTo(OrderStatus.FILLED)
        assertThat(orderStateSell2.orderStatus).isEqualTo(OrderStatus.FILLED)

        assertThat(orderStateBuy2.remainingQuantity).isEqualTo(0)
        assertThat(orderStateSell2.remainingQuantity).isEqualTo(0)

        assertThat(orderStateBuy2.tradeLegs).hasSize(1)
        assertThat(orderStateSell2.tradeLegs).hasSize(1)

        assertThat(orderStateBuy2.tradeLegs.first.order).isEqualTo(orderStateBuy2.order)
        assertThat(orderStateSell2.tradeLegs.first.order).isEqualTo(orderStateSell2.order)

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

        assertThat(orderStateBuy1.tradeLegs.first.order).isEqualTo(orderStateBuy1.order)
        assertThat(orderStateSell1Big.tradeLegs.first.order).isEqualTo(orderStateSell1Big.order)

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

        assertThat(orderStateSell1Big.tradeLegs.first.order).isEqualTo(orderStateSell1Big.order)
        assertThat(orderStateBuy1.tradeLegs.first.order).isEqualTo(orderStateBuy1.order)

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

        val order1BigRemainingQuantity = orderBuy1Big.quantity + (orderSell1Big.quantity + orderStateBuy1.order.quantity)
        assertThat(orderStateBuy1Big.remainingQuantity).isEqualTo(order1BigRemainingQuantity)

        assertThat(orderStateSell1Big.tradeLegs).hasSize(2)
        assertThat(orderStateBuy1.tradeLegs).hasSize(1)
        assertThat(orderStateBuy1Big.tradeLegs).hasSize(1)

        assertThat(orderStateSell1Big.tradeLegs[0].order).isEqualTo(orderStateSell1Big.order)
        assertThat(orderStateSell1Big.tradeLegs[1].order).isEqualTo(orderStateSell1Big.order)
        assertThat(orderStateBuy1.tradeLegs.first.order).isEqualTo(orderStateBuy1.order)
        assertThat(orderStateBuy1Big.tradeLegs.first.order).isEqualTo(orderStateBuy1Big.order)

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