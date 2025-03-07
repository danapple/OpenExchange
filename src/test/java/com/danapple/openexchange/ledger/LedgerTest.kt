package com.danapple.openexchange.ledger

import com.danapple.openexchange.TestConstants.Companion.EMPTY_TRADE_1
import com.danapple.openexchange.TestConstants.Companion.EMPTY_TRADE_2
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LedgerTest
{
    private val LEDGER = Ledger()

    @Test
    fun returnsEmptyLedger()
    {
        assertThat(LEDGER.getTrades()).isEmpty()
    }

    @Test
    fun returnsAddedTrades()
    {
        LEDGER.addTrade(EMPTY_TRADE_1);
        LEDGER.addTrade(EMPTY_TRADE_2);

        assertThat(LEDGER.getTrades()).containsExactly(EMPTY_TRADE_1, EMPTY_TRADE_2)
    }
}