package com.danapple.openexchange.orders

import com.danapple.openexchange.TestConstants.Companion.ORDER_1
import com.danapple.openexchange.TestConstants.Companion.ORDER_HOLDER_1
import com.danapple.openexchange.TestConstants.Companion.ORDER_QUANTITY_1
import com.danapple.openexchange.trades.TradeLeg
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class OrderHolderTest {
    @Test
    fun remainingQuantityEqualsInitialQuantityBeforeTradeLeg() {
        assertThat(ORDER_HOLDER_1.remainingQuantity).isEqualTo(ORDER_QUANTITY_1)
    }

    @Test
    fun remainingQuantityIsReducedByTradeLeg() {
        val tradeLeg = TradeLeg.createTradeLeg(ORDER_1, 1)
        ORDER_HOLDER_1.addTradeLeg(tradeLeg)
        assertThat(ORDER_HOLDER_1.remainingQuantity).isEqualTo(ORDER_QUANTITY_1 - 1)
    }

    @Test
    fun rejectsTradeLegWithExcessiveQuantity() {
        val tradeLeg = TradeLeg.createTradeLeg(ORDER_1, 7)
        assertThatThrownBy { ORDER_HOLDER_1.addTradeLeg(tradeLeg) }.isInstanceOf(IllegalArgumentException::class.java)
    }
}