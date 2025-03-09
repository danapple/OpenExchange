package com.danapple.openexchange

import com.danapple.openexchange.orders.OrderIdGenerator
import com.danapple.openexchange.trades.TradeIdGenerator
import com.danapple.openexchange.trades.TradeLegIdGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
open class BasicConfig() {
    @Bean
    open fun clock() : Clock {
        return Clock.systemDefaultZone()
    }

    @Bean
    open fun orderIdGenerator() : OrderIdGenerator {
        return OrderIdGenerator()
    }

    @Bean
    open fun tradeIdGenerator() : TradeIdGenerator {
        return TradeIdGenerator()
    }

    @Bean
    open fun tradeLegIdGenerator() : TradeLegIdGenerator {
        return TradeLegIdGenerator()
    }
}