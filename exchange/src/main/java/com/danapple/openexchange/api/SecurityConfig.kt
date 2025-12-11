package com.danapple.openexchange.api

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
open class SecurityConfig(private val authenticationFilter: AuthenticationFilter) {
    @Bean
    @Throws(Exception::class)
    open fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() }
            .authorizeHttpRequests { registry ->
                registry.requestMatchers("/index.html").permitAll()
                registry.requestMatchers("/app.js").permitAll()
                registry.requestMatchers("/main.css").permitAll()
                registry.requestMatchers("/orders").hasAuthority(Authorities.CUSTOMER.authority)
                registry.requestMatchers("/orders/*").hasAuthority(Authorities.CUSTOMER.authority)
                registry.requestMatchers("/instruments").hasAuthority(Authorities.CUSTOMER.authority)
                registry.requestMatchers("/instruments/*").hasAuthority(Authorities.CUSTOMER.authority)
                registry.requestMatchers("/ws").permitAll()//.hasAuthority(Authorities.CUSTOMER.authority)
            }
            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}

