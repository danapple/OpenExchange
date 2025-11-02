package com.danapple.openexchange.api

import com.danapple.openexchange.dto.OrderStates
import com.danapple.openexchange.entities.customers.Customer
import com.danapple.openexchange.entities.orders.toDto
import com.danapple.openexchange.orders.OrderState
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder.getContext

abstract class BaseApi {
    internal fun createOrderStatesResponse(vararg orderStates: OrderState, httpStatus: HttpStatus): ResponseEntity<OrderStates> {
        val returningOrderStates = orderStates.map { orderState: OrderState ->
            orderState.toDto()
        }.toList()

        return ResponseEntity(OrderStates(returningOrderStates), httpStatus)
    }

    internal fun getCustomer() : Customer {
        val customer = getContext().authentication.principal
        if (customer is Customer) {
            return customer
        } else {
            throw RuntimeException("No logged in user")
        }
    }

    internal fun createOrderStatesResponse(vararg orderStates: com.danapple.openexchange.dto.OrderState, httpStatus: HttpStatus): ResponseEntity<OrderStates> {
        return ResponseEntity(OrderStates(orderStates.toList()), httpStatus)
    }
}