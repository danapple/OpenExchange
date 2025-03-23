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
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
class OrdersApi(private val engines : Map<Instrument, Engine>, private val orderFactory: OrderFactory,
                private val customerDao: CustomerDao, private val orderQueryDao: OrderQueryDao) : BaseApi() {

    @PostMapping
    fun submitOrders(@RequestBody submitOrders: SubmitOrders, @CookieValue(value = "customerKey") customerKey: String) : ResponseEntity<OrderStates> {
        val orderStates = submitOrders.orders.map { submittedOrder ->
            logger.info("New Order for customerKey $customerKey, clientOrderId ${submittedOrder.clientOrderId}: $submitOrders")
            try {
                val customer = customerDao.getCustomer(customerKey) ?: return ResponseEntity(HttpStatus.FORBIDDEN)
                val createdOrder = orderFactory.createOrder(customer, submittedOrder.clientOrderId, submittedOrder)
                val engine = engines[createdOrder.instrument]
                val orderState =
                    OrderState(createdOrder, if (engine == null) OrderStatus.REJECTED else OrderStatus.OPEN)
                logger.info("OrderState $orderState")
                engine?.newOrder(orderState)
                convertOrderState(orderState)
            }
            catch (e : Exception) {
                logger.warn("Unable to handle new order $submittedOrder", e)
                OrderState(0, OrderStatus.REJECTED, 0, submittedOrder)
            }
        }.toTypedArray()
        return createOrderStatesResponse(orderStates = orderStates, HttpStatus.OK)
    }

    @GetMapping
    fun getOrders(@CookieValue(value = "customerKey") customerKey: String) : ResponseEntity<OrderStates> {
        OrderApi.logger.info("getOrders for customerKey $customerKey")
        val customer = customerDao.getCustomer(customerKey) ?: return ResponseEntity(HttpStatus.FORBIDDEN)
        val orderStates = orderQueryDao.getOrders(customer)
        return createOrderStatesResponse(orderStates = orderStates.toTypedArray(), HttpStatus.OK)
    }

    @DeleteMapping
    fun cancelOrders(@CookieValue(value = "customerKey") customerKey: String) : ResponseEntity<OrderStates> {
        OrderApi.logger.info("cancelOrders for customerKey $customerKey")
        val customer = customerDao.getCustomer(customerKey) ?: return ResponseEntity(HttpStatus.FORBIDDEN)
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