package com.danapple.openexchange.api

import com.danapple.openexchange.dao.CustomerDao
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.filter.GenericFilterBean
import java.io.IOException

@Service
class AuthenticationFilter(customerDao: CustomerDao) : GenericFilterBean() {

    private val authenticator = CustomerKeyCookieAuthenticator(customerDao)

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, filterChain: FilterChain) {
        try {
            val authentication = authenticator.getAuthentication(request as HttpServletRequest)
            SecurityContextHolder.getContext().authentication = authentication
            filterChain.doFilter(request, response)
        } catch (exp: Exception) {
            val httpResponse = response as HttpServletResponse
            httpResponse.status = HttpServletResponse.SC_UNAUTHORIZED
            httpResponse.contentType = MediaType.APPLICATION_JSON_VALUE
            val writer = httpResponse.writer
            writer.print(exp.message)
            writer.flush()
            writer.close()
        }
    }
}