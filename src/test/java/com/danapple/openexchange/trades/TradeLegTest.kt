package com.danapple.openexchange.trades

import com.danapple.openexchange.orders.Order
import com.danapple.openexchange.orders.Side
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import java.math.BigDecimal

class TradeLegTest
{
    private val ORDER = Order.createOrder( "C", "Order 1", Side.BUY, 5, BigDecimal.ONE)

    @Test
    fun generatesTradeLeg() {
        val tradeLeg = TradeLeg.createTradeLeg(4, ORDER)
        assertThat(tradeLeg).isNotNull()
    }

    @Test
    fun generatesUniqueTradeLegIds() {
        val tradeLeg1 = TradeLeg.createTradeLeg(4, ORDER)
        val tradeLeg2 = TradeLeg.createTradeLeg(6, ORDER)

        assertThat(tradeLeg2.tradeLegId).isNotEqualTo(tradeLeg1.tradeLegId)
    }

    @Test
    fun refusesToGenerateTradeLegWithZeroQuantity() {
        assertThatThrownBy {  TradeLeg.createTradeLeg(0, ORDER) }.isInstanceOf(
            IllegalArgumentException::class.java).hasMessageContaining("not 0")
    }

    @Test
    fun refusesToGenerateTradeLegWithNegativeQuantity() {
        assertThatThrownBy {  TradeLeg.createTradeLeg(-7, ORDER) }.isInstanceOf(
            IllegalArgumentException::class.java).hasMessageContaining("not -7")
    }
}