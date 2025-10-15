package com.danapple.openexchange.orders

import com.danapple.openexchange.UnitTest
import com.danapple.openexchange.dto.OrderStatus
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class OrderStateTest : UnitTest() {
    private val orderState1 = OrderState(ORDER_BUY_1, ORDER_CREATETIME_1)
    private val trade1 = tradeFactory.createTrade(CLOCK.millis(), BigDecimal.ONE)

    @Test
    fun remainingQuantityEqualsInitialQuantityBeforeTradeLeg() {
        assertThat(orderState1.remainingQuantity).isEqualTo(ORDER_QUANTITY_1)
    }

    @Test
    fun remainingQuantityIsReducedByTradeLeg() {
        tradeLegFactory.createTradeLeg(orderState1, trade1, 1)
        assertThat(orderState1.remainingQuantity).isEqualTo(ORDER_QUANTITY_1 - 1)
    }

    @Test
    fun filledQuantityIsIncreasedByTradeLeg() {
        tradeLegFactory.createTradeLeg(orderState1, trade1, 3)
        assertThat(orderState1.filledQuantity()).isEqualTo(3)
    }

    @Test
    fun rejectsTradeLegWithExcessiveQuantity() {
        assertThatThrownBy {tradeLegFactory.createTradeLeg(orderState1, trade1, ORDER_QUANTITY_1 + 2) }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun partialFillDoesNotChangeOrderStatus() {
        tradeLegFactory.createTradeLeg(orderState1, trade1,1)
        assertThat(orderState1.orderStatus).isEqualTo(OrderStatus.OPEN)
        assertThat(orderState1.orderStatus.viability).isEqualTo(OrderStatus.Viability.ALIVE)
    }

    @Test
    fun fillChangesOrderStatusToFilled() {
        tradeLegFactory.createTradeLeg(orderState1, trade1, ORDER_BUY_1.quantity)
        assertThat(orderState1.orderStatus).isEqualTo(OrderStatus.FILLED)
        assertThat(orderState1.orderStatus.viability).isEqualTo(OrderStatus.Viability.DEAD)
    }
}