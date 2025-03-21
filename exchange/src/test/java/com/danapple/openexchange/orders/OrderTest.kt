package com.danapple.openexchange.orders

import com.danapple.openexchange.TestConstants.Companion.CL_ORD_BUY_1
import com.danapple.openexchange.TestConstants.Companion.CUSTOMER
import com.danapple.openexchange.TestConstants.Companion.EQUITY_1
import com.danapple.openexchange.TestConstants.Companion.ORDER_BUY_1
import com.danapple.openexchange.TestConstants.Companion.ORDER_FACTORY
import com.danapple.openexchange.TestConstants.Companion.ORDER_TIMESTAMP_1
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class OrderTest {
    @Test
    fun generatesOrder() {
        val order = ORDER_FACTORY.createOrder(CUSTOMER, ORDER_TIMESTAMP_1, CL_ORD_BUY_1, EQUITY_1, BigDecimal.TEN, 5)
        assertThat(ORDER_BUY_1).isNotNull()
        assertThat(order.quantity).isEqualTo(5)
        assertThat(order.price).isEqualTo(BigDecimal.TEN)
    }

    @Test
    fun generatesOrdersWithDistinctOrderIds() {
        val order1 = ORDER_FACTORY.createOrder(CUSTOMER, ORDER_TIMESTAMP_1, CL_ORD_BUY_1, EQUITY_1, BigDecimal.ONE, 5)
        val order2 = ORDER_FACTORY.createOrder(CUSTOMER, ORDER_TIMESTAMP_1, CL_ORD_BUY_1, EQUITY_1, BigDecimal.ONE, 5)

        assertThat(order2.orderId).isNotEqualTo(order1.orderId)
    }

    @Test
    fun refusesToGenerateOrderWithZeroQuantity() {
        assertThatThrownBy { ORDER_FACTORY.createOrder(CUSTOMER, ORDER_TIMESTAMP_1, CL_ORD_BUY_1, EQUITY_1, BigDecimal.ONE, 0)}.isInstanceOf(IllegalArgumentException::class.java).hasMessageContaining("must be non-zero")
    }

    @Test
    fun refusesToGenerateOrderWithZeroPrice() {
        assertThatThrownBy { ORDER_FACTORY.createOrder(CUSTOMER, ORDER_TIMESTAMP_1, CL_ORD_BUY_1, EQUITY_1, BigDecimal.ZERO, 3)}.isInstanceOf(IllegalArgumentException::class.java).hasMessageContaining("must be positive")
    }

    @Test
    fun refusesToGenerateOrderWithNegativePrice() {
        assertThatThrownBy { ORDER_FACTORY.createOrder(CUSTOMER, ORDER_TIMESTAMP_1, CL_ORD_BUY_1, EQUITY_1, BigDecimal.TEN.negate(), 4)}.isInstanceOf(IllegalArgumentException::class.java).hasMessageContaining("must be positive")
    }

    @Test
    fun indicatesBuyOrderNotSellOrderForPositiveQuantity () {
        val order1 = ORDER_FACTORY.createOrder(CUSTOMER, ORDER_TIMESTAMP_1, CL_ORD_BUY_1, EQUITY_1, BigDecimal.ONE, 5)
        assertThat(order1.isBuyOrder()).isTrue()
        assertThat(order1.isSellOrder()).isFalse()
    }

    @Test
    fun indicatesSellOrderNotBuyOrderForPositiveQuantity () {
        val order1 = ORDER_FACTORY.createOrder(CUSTOMER, ORDER_TIMESTAMP_1, CL_ORD_BUY_1, EQUITY_1, BigDecimal.ONE, -7)
        assertThat(order1.isSellOrder()).isTrue()
        assertThat(order1.isBuyOrder()).isFalse()
    }
}