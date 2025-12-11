package com.danapple.openexchange.api

import com.danapple.openexchange.entities.customers.Customer
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class CustomerKeyAuthentication(authorities: Collection<GrantedAuthority?>?,
                                private val principal : Customer) : AbstractAuthenticationToken(authorities) {
    init {
        isAuthenticated = true
    }

    override fun getCredentials(): Any? {
        return null
    }

    override fun getPrincipal(): Any {
        return principal
    }
}