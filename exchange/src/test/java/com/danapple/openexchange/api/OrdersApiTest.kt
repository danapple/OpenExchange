package com.danapple.openexchange.api

import com.danapple.openexchange.TestConstants
import com.danapple.openexchange.TestConstants.Companion.ORDER_BUY_1
import com.danapple.openexchange.TestConstants.Companion.ORDER_SELL_1
import com.danapple.openexchange.dto.OrderStates
import com.danapple.openexchange.dto.SubmitOrders
import com.danapple.openexchange.entities.orders.toDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class OrdersApiTest : AbstractRestTest() {

    @BeforeEach
    fun beforeEach() {
        customerKey = TestConstants.CUSTOMER_KEY_1
    }

    @Disabled
    @Test
    fun getAllOrdersReturnsEmptyOrders() {
        val getResponse: ResponseEntity<OrderStates> =
            template!!.getForEntity("/orders", OrderStates::class.java)
        Assertions.assertEquals(HttpStatus.OK, getResponse.statusCode)
        Assertions.assertNotNull(getResponse.body)

        assertThat(getResponse.body.orderStates).isEmpty()
    }

    @Test
    fun getAllOrdersReturnsSingleSubmittedOrder() {
        template!!.postForEntity("/orders", SubmitOrders(listOf(ORDER_BUY_1.toDtoOrder())), OrderStates::class.java)

        val getResponse: ResponseEntity<OrderStates> =
            template!!.getForEntity("/orders", OrderStates::class.java)
        Assertions.assertEquals(HttpStatus.OK, getResponse.statusCode)
        Assertions.assertNotNull(getResponse.body)

        assertThat(getResponse.body.orderStates).isNotEmpty
        val returnedOrderStatesByClientOrderId =
            getResponse.body.orderStates.associateBy { orderState -> orderState.order.clientOrderId }
        assertThat(returnedOrderStatesByClientOrderId[ORDER_BUY_1.clientOrderId]).isNotNull
    }

    @Test
    fun getAllOrdersReturnsMultipleSubmittedOrders() {
        template!!.postForEntity(
            "/orders",
            SubmitOrders(listOf(ORDER_BUY_1.toDto(), ORDER_SELL_1.toDtoOrder())),
            OrderStates::class.java
        )

        val getResponse: ResponseEntity<OrderStates> =
            template!!.getForEntity("/orders", OrderStates::class.java)
        Assertions.assertEquals(HttpStatus.OK, getResponse.statusCode)
        Assertions.assertNotNull(getResponse.body)

        val returnedOrderStatesByClientOrderId =
            getResponse.body.orderStates.associateBy { orderState -> orderState.order.clientOrderId }
        assertThat(returnedOrderStatesByClientOrderId[ORDER_BUY_1.clientOrderId]).isNotNull
        assertThat(returnedOrderStatesByClientOrderId[ORDER_SELL_1.clientOrderId]).isNotNull

    }

    @Test
    fun submitOrdersReturnsSingleSubmittedOrder() {
        val submitResponse: ResponseEntity<OrderStates> =
            template!!.postForEntity("/orders", SubmitOrders(listOf(ORDER_BUY_1.toDtoOrder())), OrderStates::class.java)

        Assertions.assertEquals(HttpStatus.OK, submitResponse.statusCode)
        Assertions.assertNotNull(submitResponse.body)

        assertThat(submitResponse.body!!.orderStates.first().order.clientOrderId).isEqualTo(ORDER_BUY_1.clientOrderId);
    }
}