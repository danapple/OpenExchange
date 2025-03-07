package com.danapple.openexchange.trades

import com.danapple.openexchange.TestConstants.Companion.ORDER_1
import org.assertj.core.api.AssertionsForClassTypes
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class TradeTest {
    @Test
    fun acceptsBalancedTrade() {
        val tradeLeg1 = TradeLeg.createTradeLeg(ORDER_1, 1)
        val tradeLeg2 = TradeLeg.createTradeLeg(ORDER_1, -1)
        val trade = Trade.createTrade(BigDecimal.ONE, setOf(tradeLeg1, tradeLeg2))
        AssertionsForClassTypes.assertThat(trade.tradeLegs)
    }

    @Test
    fun rejectsUnbalancedTrade() {
        val tradeLeg1 = TradeLeg.createTradeLeg(ORDER_1, 2)
        val tradeLeg2 = TradeLeg.createTradeLeg(ORDER_1, -1)
        AssertionsForClassTypes.assertThatThrownBy {
            Trade.createTrade(
                BigDecimal.ONE,
                java.util.Set.of(tradeLeg1, tradeLeg2)
            )
        }
            .isInstanceOf(java.lang.IllegalArgumentException::class.java).hasMessageContaining("must be 0, not 1")
    }
}
