package com.danapple.openexchange

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
open class BasicConfig {
    @Bean
    open fun clock() : Clock {
        return Clock.systemDefaultZone()
    }
}