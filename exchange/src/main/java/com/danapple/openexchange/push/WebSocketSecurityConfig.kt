package com.danapple.openexchange.push

import com.danapple.openexchange.api.Authorities
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.messaging.simp.SimpMessageType
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager


@Configuration
@EnableWebSocketSecurity
open class WebSocketSecurityConfig {
    @Bean
    open fun messageAuthorizationManager(
        messages: MessageMatcherDelegatingAuthorizationManager.Builder
    ): AuthorizationManager<Message<*>> {
        messages
            .simpDestMatchers("/app/echotest", "/topics/depth","/topics/trades","/topics/executions/*")
            .hasAuthority(Authorities.CUSTOMER.authority)
            .simpSubscribeDestMatchers("/topics/depth","/topics/trades","/topics/executions/*")
            .hasAuthority(Authorities.CUSTOMER.authority)
            .simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.DISCONNECT, SimpMessageType.OTHER).permitAll()
            .anyMessage().denyAll()
        return messages.build()
    }

}

