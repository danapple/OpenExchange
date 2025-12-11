package com.danapple.openexchange.orders

import com.danapple.openexchange.UnitTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class OrderTest : UnitTest() {
    @Test
    fun generatesOrder() {
        val order =
            orderFactory.createOrder(CUSTOMER, ORDER_CREATETIME_1, CL_ORD_BUY_1, INSTRUMENT_1, BigDecimal.TEN, 5)
        assertThat(ORDER_BUY_1).isNotNull()
        assertThat(order.quantity).isEqualTo(5)
        assertThat(order.price).isEqualTo(BigDecimal.TEN)
    }

    @Test
    fun generatesOrdersWithDistinctOrderIds() {
        val order1 =
            orderFactory.createOrder(CUSTOMER, ORDER_CREATETIME_1, CL_ORD_BUY_1, INSTRUMENT_1, BigDecimal.ONE, 5)
        val order2 =
            orderFactory.createOrder(CUSTOMER, ORDER_CREATETIME_1, CL_ORD_BUY_1, INSTRUMENT_1, BigDecimal.ONE, 5)

        assertThat(order2.orderId).isNotEqualTo(order1.orderId)
    }

    @Test
    fun refusesToGenerateOrderWithZeroQuantity() {
        assertThatThrownBy {
            orderFactory.createOrder(
                CUSTOMER,
                ORDER_CREATETIME_1,
                CL_ORD_BUY_1,
                INSTRUMENT_1,
                BigDecimal.ONE,
                0
            )
        }.isInstanceOf(IllegalArgumentException::class.java).hasMessageContaining("must be non-zero")
    }

    @Test
    fun refusesToGenerateOrderWithZeroPrice() {
        assertThatThrownBy {
            orderFactory.createOrder(
                CUSTOMER,
                ORDER_CREATETIME_1,
                CL_ORD_BUY_1,
                INSTRUMENT_1,
                BigDecimal.ZERO,
                3
            )
        }.isInstanceOf(IllegalArgumentException::class.java).hasMessageContaining("must be positive")
    }

    @Test
    fun refusesToGenerateOrderWithNegativePrice() {
        assertThatThrownBy {
            orderFactory.createOrder(
                CUSTOMER,
                ORDER_CREATETIME_1,
                CL_ORD_BUY_1,
                INSTRUMENT_1,
                BigDecimal.TEN.negate(),
                4
            )
        }.isInstanceOf(IllegalArgumentException::class.java).hasMessageContaining("must be positive")
    }

    @Test
    fun indicatesBuyOrderNotSellOrderForPositiveQuantity() {
        val order1 =
            orderFactory.createOrder(CUSTOMER, ORDER_CREATETIME_1, CL_ORD_BUY_1, INSTRUMENT_1, BigDecimal.ONE, 5)
        assertThat(order1.isBuyOrder()).isTrue()
        assertThat(order1.isSellOrder()).isFalse()
    }

    @Test
    fun indicatesSellOrderNotBuyOrderForPositiveQuantity() {
        val order1 =
            orderFactory.createOrder(CUSTOMER, ORDER_CREATETIME_1, CL_ORD_BUY_1, INSTRUMENT_1, BigDecimal.ONE, -7)
        assertThat(order1.isSellOrder()).isTrue()
        assertThat(order1.isBuyOrder()).isFalse()
    }
}