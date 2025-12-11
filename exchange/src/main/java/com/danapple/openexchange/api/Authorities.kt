package com.danapple.openexchange.api

import org.springframework.security.core.GrantedAuthority

enum class Authorities : GrantedAuthority {
    CUSTOMER,
    ADMIN;

    override fun getAuthority(): String {
        return name
    }
}