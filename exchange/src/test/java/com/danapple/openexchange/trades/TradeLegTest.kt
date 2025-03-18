package com.danapple.openexchange.trades

import com.danapple.openexchange.TestConstants.Companion.ORDER_1
import com.danapple.openexchange.TestConstants.Companion.TRADE_LEG_FACTORY
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class TradeLegTest
{
    @Test
    fun generatesTradeLeg() {
        val tradeLeg = TRADE_LEG_FACTORY.createTradeLeg(ORDER_1, 4)
        assertThat(tradeLeg).isNotNull()
    }

    @Test
    fun refusesToGenerateTradeLegWithZeroQuantity() {
        assertThatThrownBy {  TRADE_LEG_FACTORY.createTradeLeg(ORDER_1, 0) }.isInstanceOf(
            IllegalArgumentException::class.java).hasMessageContaining("must be non-zero")
    }
}