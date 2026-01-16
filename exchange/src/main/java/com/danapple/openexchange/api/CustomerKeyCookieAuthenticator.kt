package com.danapple.openexchange.api

import com.danapple.openexchange.dao.CustomerDao
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class CustomerKeyCookieAuthenticator(private val customerDao: CustomerDao) {
    private val customerAuthorityList: List<GrantedAuthority> = listOf(Authorities.CUSTOMER)
    private val adminAuthorityList: List<GrantedAuthority> = listOf(Authorities.ADMIN)

    fun getAuthentication(request: HttpServletRequest): Authentication {
        for (cookie in request.cookies) {
            if (cookie.name.equals("customerKey")) {
                val customer = customerDao.getCustomer(cookie.value)
                if (customer != null) {
                    return CustomerKeyAuthentication(customerAuthorityList, customer)
                }
            }
        }
        throw BadCredentialsException("Invalid customerKey")
    }
}