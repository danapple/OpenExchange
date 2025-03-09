package com.danapple.openexchange.trades

import com.danapple.openexchange.TestConstants.Companion.ORDER_1
import com.danapple.openexchange.TestConstants.Companion.TRADE_FACTORY
import com.danapple.openexchange.TestConstants.Companion.TRADE_LEG_FACTORY
import com.danapple.openexchange.TestConstants.Companion.TRADE_TIMESTAMP_1
import org.assertj.core.api.AssertionsForClassTypes
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class TradeTest {
    @Test
    fun createsBalancedTrade() {
        val tradeLeg1 = TRADE_LEG_FACTORY.createTradeLeg(ORDER_1, 1)
        val tradeLeg2 = TRADE_LEG_FACTORY.createTradeLeg(ORDER_1, -1)
        TRADE_FACTORY.createTrade(TRADE_TIMESTAMP_1, BigDecimal.ONE, setOf(tradeLeg1, tradeLeg2))
    }

    @Test
    fun rejectsUnbalancedTrade() {
        val tradeLeg1 = TRADE_LEG_FACTORY.createTradeLeg(ORDER_1, 2)
        val tradeLeg2 = TRADE_LEG_FACTORY.createTradeLeg(ORDER_1, -1)
        AssertionsForClassTypes.assertThatThrownBy {
            TRADE_FACTORY.createTrade(
                TRADE_TIMESTAMP_1,
                BigDecimal.ONE,
                java.util.Set.of(tradeLeg1, tradeLeg2)
            )
        }
            .isInstanceOf(java.lang.IllegalArgumentException::class.java).hasMessageContaining("must be 0, not 1")
    }
}
