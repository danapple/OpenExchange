package com.danapple.openexchange.orders

import com.danapple.openexchange.trades.TradeLeg
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class OrderHolderTest
{
    private val ORDER = Order.createOrder("C", Side.BUY, 5, BigDecimal.ONE)
    private val ORDER_HOLDER = OrderHolder(ORDER)

    @Test
    fun remainingQuantityEqualsInitialQuantityBeforeTradeLeg()
    {
        assertThat(ORDER_HOLDER.remainingQuantity).isEqualTo(5)
    }

    @Test
    fun remainingQuantityIsReducedByTradeLeg()
    {
        val tradeLeg = TradeLeg.createTradeLeg(3, ORDER)
        ORDER_HOLDER.addTradeLeg(tradeLeg)
        assertThat(ORDER_HOLDER.remainingQuantity).isEqualTo(2)
    }

    @Test
    fun rejectsTradeLegWithExcessiveQuantity()
    {
        val tradeLeg = TradeLeg.createTradeLeg(7, ORDER)
        assertThatThrownBy { ORDER_HOLDER.addTradeLeg(tradeLeg) } .isInstanceOf(IllegalArgumentException::class.java)
    }
}