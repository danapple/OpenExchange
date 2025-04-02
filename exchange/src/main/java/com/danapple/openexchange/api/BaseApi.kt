package com.danapple.openexchange.api

import com.danapple.openexchange.dto.Order
import com.danapple.openexchange.dto.OrderLeg
import com.danapple.openexchange.dto.OrderStates
import com.danapple.openexchange.entities.customers.Customer
import com.danapple.openexchange.orders.OrderState
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder.getContext

abstract class BaseApi {
    internal fun createOrderStatesResponse(vararg orderStates: OrderState, httpStatus: HttpStatus): ResponseEntity<OrderStates> {
        val returningOrderStates = orderStates.map { orderState: OrderState ->
            val returningOrder = Order(
                orderState.order.clientOrderId,
                orderState.order.price,
                orderState.order.quantity,
                listOf(OrderLeg(orderState.order.instrument.instrumentId, 1))
            )
            com.danapple.openexchange.dto.OrderState(
                orderState.order.createTime,
                orderState.orderStatus,
                orderState.remainingQuantity,
                returningOrder
            )
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