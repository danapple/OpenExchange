package com.danapple.openexchange.entities.customers

import java.security.Principal

data class Customer(val customerId: Long, val customerKey: String) : Principal {
    override fun getName(): String {
        return "%s".format(customerId)
    }
}
