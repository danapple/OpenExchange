package com.danapple.openexchange.orders

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import java.math.BigDecimal

class OrderTest
{
    @Test
    fun generatesOrder() {
        val order = Order.createOrder("IBM", Side.BUY, 5, BigDecimal.TEN)
        assertThat(order).isNotNull()
        assertThat(order.symbol).isEqualTo("IBM")
        assertThat(order.side).isEqualTo(Side.BUY)
        assertThat(order.quantity).isEqualTo(5)
        assertThat(order.price).isEqualTo(BigDecimal.TEN)
    }

    @Test
    fun generatesOrdersWithIncreasingOrderIds() {
        val order1 = Order.createOrder("IBM", Side.BUY, 5, BigDecimal.TEN)
        val order2 = Order.createOrder("IBM", Side.BUY, 8, BigDecimal.ONE)

        assertThat(order2.orderId).isGreaterThan(order1.orderId)
    }

    @Test
    fun refusesToGenerateOrderWithZeroQuantity() {
        assertThatThrownBy { Order.createOrder("IBM", Side.BUY, 0, BigDecimal.TEN) }.isInstanceOf(IllegalArgumentException::class.java).hasMessageContaining("not 0")
    }

    @Test
    fun refusesToGenerateOrderWithNegativeQuantity() {
        assertThatThrownBy { Order.createOrder("IBM", Side.BUY, -4, BigDecimal.TEN) }.isInstanceOf(IllegalArgumentException::class.java).hasMessageContaining("not -4")
    }

    @Test
    fun refusesToGenerateOrderWithZeroPrice() {
        assertThatThrownBy { Order.createOrder("IBM", Side.BUY, 1, BigDecimal.ZERO) }.isInstanceOf(IllegalArgumentException::class.java).hasMessageContaining("not 0")
    }

    @Test
    fun refusesToGenerateOrderWithNegativePrice() {
        assertThatThrownBy { Order.createOrder("IBM", Side.BUY, -4, BigDecimal.TEN.negate()) }.isInstanceOf(IllegalArgumentException::class.java).hasMessageContaining("not -4")
    }

    @Test
    fun refusesToGenerateOrderWithEmptySymbol() {
        assertThatThrownBy { Order.createOrder("", Side.BUY, 3, BigDecimal.TEN) }.isInstanceOf(IllegalArgumentException::class.java).hasMessageContaining("blank")
    }

    @Test
    fun refusesToGenerateOrderWithWhitespaceSymbol() {
        assertThatThrownBy { Order.createOrder("   ", Side.BUY, 11, BigDecimal.ONE) }.isInstanceOf(IllegalArgumentException::class.java).hasMessageContaining("blank")
    }
}