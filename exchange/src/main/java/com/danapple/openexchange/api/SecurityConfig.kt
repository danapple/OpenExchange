package com.danapple.openexchange.api

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
open class SecurityConfig (private val authenticationFilter : AuthenticationFilter){
    @Bean
    @Throws(Exception::class)
    open fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() }
            .authorizeHttpRequests { authorizationManagerRequestMatcherRegistry ->
                authorizationManagerRequestMatcherRegistry.requestMatchers(
                    "/orders"
                ).hasAuthority(Authorities.CUSTOMER.authority)
                authorizationManagerRequestMatcherRegistry.requestMatchers(
                    "/orders/*"
                ).hasAuthority(Authorities.CUSTOMER.authority)
                authorizationManagerRequestMatcherRegistry.requestMatchers(
                    "/instruments"
                ).hasAuthority(Authorities.CUSTOMER.authority)
                authorizationManagerRequestMatcherRegistry.requestMatchers(
                    "/instruments/*"
                ).hasAuthority(Authorities.CUSTOMER.authority)
                authorizationManagerRequestMatcherRegistry.requestMatchers(
                    "/exchangewebsockets"
                ).hasAuthority(Authorities.CUSTOMER.authority)
            }
            .httpBasic(Customizer.withDefaults())
            .sessionManagement { httpSecuritySessionManagementConfigurer: SessionManagementConfigurer<HttpSecurity?> ->
                httpSecuritySessionManagementConfigurer.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            }
            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}

