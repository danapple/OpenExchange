package com.danapple.openexchange.trades

import com.danapple.openexchange.UnitTest
import com.danapple.openexchange.orders.OrderState
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class TradeTest  : UnitTest() {
    private val orderState1 = OrderState(ORDER_BUY_1)
    private val trade1 = tradeFactory.createTrade(CLOCK.millis(), BigDecimal.ONE)

    @Test
    fun addsTradeLeg() {
        val tradeLeg1 = tradeLegFactory.createTradeLeg(orderState1, trade1,1)
        trade1.addTradeLeg(tradeLeg1)
        assertThat(trade1.tradeLegs).containsExactly(tradeLeg1)
    }
}
