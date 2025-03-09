package com.danapple.openexchange.orders

import com.danapple.openexchange.TestConstants.Companion.ORDER_1
import com.danapple.openexchange.TestConstants.Companion.ORDER_QUANTITY_1
import com.danapple.openexchange.TestConstants.Companion.TRADE_LEG_FACTORY
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class OrderStateTest {
    private val orderState1 = OrderState(ORDER_1)

    @Test
    fun remainingQuantityEqualsInitialQuantityBeforeTradeLeg() {
        assertThat(orderState1.remainingQuantity).isEqualTo(ORDER_QUANTITY_1)
    }

    @Test
    fun remainingQuantityIsReducedByTradeLeg() {
        val tradeLeg = TRADE_LEG_FACTORY.createTradeLeg(ORDER_1, 1)
        orderState1.addTradeLeg(tradeLeg)
        assertThat(orderState1.remainingQuantity).isEqualTo(ORDER_QUANTITY_1 - 1)
    }

    @Test
    fun rejectsTradeLegWithExcessiveQuantity() {
        val tradeLeg = TRADE_LEG_FACTORY.createTradeLeg(ORDER_1, 7)
        assertThatThrownBy { orderState1.addTradeLeg(tradeLeg) }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun partialFillDoesNotChangeOrderStatus() {
        val tradeLeg = TRADE_LEG_FACTORY.createTradeLeg(ORDER_1, 1)
        orderState1.addTradeLeg(tradeLeg)
        assertThat(orderState1.orderStatus).isEqualTo(OrderStatus.OPEN)
        assertThat(orderState1.orderStatus.viability).isEqualTo(OrderStatus.Viability.ALIVE)
    }

    @Test
    fun fillChangesOrderStatusToFilled() {
        val tradeLeg = TRADE_LEG_FACTORY.createTradeLeg(ORDER_1, ORDER_1.quantity)
        orderState1.addTradeLeg(tradeLeg)
        assertThat(orderState1.orderStatus).isEqualTo(OrderStatus.FILLED)
        assertThat(orderState1.orderStatus.viability).isEqualTo(OrderStatus.Viability.DEAD)
    }
}