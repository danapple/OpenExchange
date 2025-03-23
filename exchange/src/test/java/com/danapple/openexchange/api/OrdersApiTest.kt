package com.danapple.openexchange.api

import com.danapple.openexchange.dto.OrderState
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class OrdersApiTest : AbstractRestTest() {
    @Test
    fun getAllOrders() {
        val response: ResponseEntity<Array<OrderState>> =
            template!!.getForEntity(base.toString() + "/orders", Array<OrderState>::class.java)
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode())
        Assertions.assertNotNull(response.getBody())
    }
}