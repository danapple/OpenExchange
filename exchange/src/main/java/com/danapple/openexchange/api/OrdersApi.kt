package com.danapple.openexchange.api

import com.danapple.openexchange.dao.CustomerDao
import com.danapple.openexchange.dao.OrderQueryDao
import com.danapple.openexchange.dto.*
import com.danapple.openexchange.engine.Engine
import com.danapple.openexchange.entities.instruments.Instrument
import com.danapple.openexchange.orders.OrderFactory
import com.danapple.openexchange.orders.OrderState
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
class OrdersApi(@Autowired val engines : Map<Instrument, Engine>, @Autowired val orderFactory: OrderFactory,
                @Autowired val customerDao: CustomerDao, @Autowired val orderQueryDao: OrderQueryDao) : BaseApi() {

    @PostMapping
    fun submitOrders(@RequestBody submitOrders: SubmitOrders, @CookieValue(value = "customerId") customerId: String) : ResponseEntity<OrderStates> {
        val orderStates = submitOrders.orders.map { submittedOrder ->
            logger.info("New Order for customerId $customerId, clientOrderId ${submittedOrder.clientOrderId}: $submitOrders")
            try {
                val customer = customerDao.getCustomer(customerId) ?: return ResponseEntity(HttpStatus.FORBIDDEN)
                val createdOrder = orderFactory.createOrder(customer, submittedOrder.clientOrderId, submittedOrder)
                val engine = engines[createdOrder.instrument]
                val orderState =
                    OrderState(createdOrder, if (engine == null) OrderStatus.REJECTED else OrderStatus.OPEN)
                logger.info("OrderState $orderState")
                engine?.newOrder(orderState)
                convertOrderState(orderState)
            }
            catch (e : Exception) {
                OrderState(0, OrderStatus.REJECTED, 0, submittedOrder)
            }
        }.toTypedArray()
        return createOrderStatesResponse(orderStates = orderStates, HttpStatus.OK)
    }

    @GetMapping
    fun getOrders(@CookieValue(value = "customerId") customerId: String) : ResponseEntity<OrderStates> {
        OrderApi.logger.info("getOrders for customerId $customerId")
        val customer = customerDao.getCustomer(customerId) ?: return ResponseEntity(HttpStatus.FORBIDDEN)
        val orderStates = orderQueryDao.getOrders(customer)
        return createOrderStatesResponse(orderStates = orderStates.toTypedArray(), HttpStatus.OK)
    }

    @DeleteMapping
    fun cancelOrders(@CookieValue(value = "customerId") customerId: String) : ResponseEntity<OrderStates> {
        OrderApi.logger.info("cancelOrders for customerId $customerId")
        val customer = customerDao.getCustomer(customerId) ?: return ResponseEntity(HttpStatus.FORBIDDEN)
        val orderStatesToCancel = orderQueryDao.getOrders(customer)
        val resultingOrderStates = orderStatesToCancel.map{ orderState ->
            try {
                engines[orderState.order.instrument]?.cancelOrder(orderState)
            } catch (e: Exception) {
                logger.warn("Unable to cancel order $orderState", e);
            }
            convertOrderState(orderState)
        }.toTypedArray()
        return createOrderStatesResponse(orderStates = resultingOrderStates, HttpStatus.OK)
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(OrdersApi::class.java)
    }
}