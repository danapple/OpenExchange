package com.danapple.openexchange.engine

import com.danapple.openexchange.TestConstants.Companion.ENGINE_FACTORY
import com.danapple.openexchange.TestConstants.Companion.EQUITY_1
import org.junit.jupiter.api.Test

class EngineTest {
    val engine = ENGINE_FACTORY.createEngines(setOf(EQUITY_1))

    @Test
    fun nullTest() {
    }

}