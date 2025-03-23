package com.danapple.openexchange.trades

import com.danapple.openexchange.TestConstants.Companion.CLOCK
import com.danapple.openexchange.TestConstants.Companion.ORDER_BUY_1
import com.danapple.openexchange.TestConstants.Companion.TRADE_FACTORY
import com.danapple.openexchange.TestConstants.Companion.TRADE_LEG_FACTORY
import com.danapple.openexchange.orders.OrderState
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class TradeLegTest
{
    private val orderState1 = OrderState(ORDER_BUY_1)
    private val trade1 = TRADE_FACTORY.createTrade(CLOCK.millis(), BigDecimal.ONE)

    @Test
    fun generatesTradeLeg() {
        val tradeLeg = TRADE_LEG_FACTORY.createTradeLeg(orderState1, trade1, 4)
        assertThat(tradeLeg).isNotNull()
    }

    @Test
    fun addsTradeLegToOrderState() {
        val tradeLeg = TRADE_LEG_FACTORY.createTradeLeg(orderState1, trade1, 4)
        assertThat(orderState1.tradeLegs).containsExactly(tradeLeg)
    }

    @Test
    fun addsTradeLegToTrade() {
        val tradeLeg = TRADE_LEG_FACTORY.createTradeLeg(orderState1, trade1, 4)
        assertThat(trade1.tradeLegs).containsExactly(tradeLeg)
    }

    @Test
    fun refusesToGenerateTradeLegWithZeroQuantity() {
        assertThatThrownBy {  TRADE_LEG_FACTORY.createTradeLeg(orderState1, trade1, 0) }.isInstanceOf(
            IllegalArgumentException::class.java).hasMessageContaining("must be non-zero")
    }
}