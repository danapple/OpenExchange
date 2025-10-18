package com.danapple.openexchange.push

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.messaging.support.ChannelInterceptor

@Configuration
open class CsrfChannelInterceptorConfigurer {
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean("csrfChannelInterceptor")
    open fun noopCsrfChannelInterceptor(): ChannelInterceptor {
        return object : ChannelInterceptor {}
    }
}