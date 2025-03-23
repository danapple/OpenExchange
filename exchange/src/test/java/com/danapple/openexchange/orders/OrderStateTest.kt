package com.danapple.openexchange.orders

import com.danapple.openexchange.TestConstants.Companion.CLOCK
import com.danapple.openexchange.TestConstants.Companion.ORDER_BUY_1
import com.danapple.openexchange.TestConstants.Companion.ORDER_QUANTITY_1
import com.danapple.openexchange.TestConstants.Companion.TRADE_FACTORY
import com.danapple.openexchange.TestConstants.Companion.TRADE_LEG_FACTORY
import com.danapple.openexchange.dto.OrderStatus
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class OrderStateTest {
    private val orderState1 = OrderState(ORDER_BUY_1)
    private val trade1 = TRADE_FACTORY.createTrade(CLOCK.millis(), BigDecimal.ONE)

    @Test
    fun remainingQuantityEqualsInitialQuantityBeforeTradeLeg() {
        assertThat(orderState1.remainingQuantity).isEqualTo(ORDER_QUANTITY_1)
    }

    @Test
    fun remainingQuantityIsReducedByTradeLeg() {
        TRADE_LEG_FACTORY.createTradeLeg(orderState1, trade1, 1)
        assertThat(orderState1.remainingQuantity).isEqualTo(ORDER_QUANTITY_1 - 1)
    }

    @Test
    fun rejectsTradeLegWithExcessiveQuantity() {
        assertThatThrownBy {TRADE_LEG_FACTORY.createTradeLeg(orderState1, trade1, ORDER_QUANTITY_1 + 2) }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun partialFillDoesNotChangeOrderStatus() {
        TRADE_LEG_FACTORY.createTradeLeg(orderState1, trade1,1)
        assertThat(orderState1.orderStatus).isEqualTo(OrderStatus.OPEN)
        assertThat(orderState1.orderStatus.viability).isEqualTo(OrderStatus.Viability.ALIVE)
    }

    @Test
    fun fillChangesOrderStatusToFilled() {
        TRADE_LEG_FACTORY.createTradeLeg(orderState1, trade1, ORDER_BUY_1.quantity)
        assertThat(orderState1.orderStatus).isEqualTo(OrderStatus.FILLED)
        assertThat(orderState1.orderStatus.viability).isEqualTo(OrderStatus.Viability.DEAD)
    }
}