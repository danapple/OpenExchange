package com.danapple.openexchange.api

import com.danapple.openexchange.api.OrderApi.Companion
import com.danapple.openexchange.dao.CustomerDao
import com.danapple.openexchange.dao.OrderDao
import com.danapple.openexchange.dto.*
import com.danapple.openexchange.engine.Engine
import com.danapple.openexchange.instruments.Instrument
import com.danapple.openexchange.orders.OrderFactory
import com.danapple.openexchange.orders.OrderState
import com.danapple.openexchange.orders.OrderStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
class OrdersApi(@Autowired val engines : Map<Instrument, Engine>, @Autowired val orderFactory: OrderFactory,
                @Autowired val customerDao: CustomerDao, @Autowired val orderDao: OrderDao) : BaseApi() {

    @PostMapping
    fun submitOrders(@RequestBody submitOrders: SubmitOrders, @CookieValue(value = "customerId") customerId: String) : ResponseEntity<OrderStates> {

        val orderStates = submitOrders.orders.map { submittedOrder ->
            logger.info("new Order for customerId $customerId, clientOrderId ${submittedOrder.clientOrderId}: $submitOrders")
            try {
                val createdOrder = orderFactory.createOrder(customerId, submittedOrder.clientOrderId, submittedOrder)
                val engine = engines[createdOrder.instrument]
                val orderState =
                    OrderState(createdOrder, if (engine == null) OrderStatus.REJECTED else OrderStatus.OPEN)
                logger.info("OrderState $orderState")
                if (engine != null) {
                    orderDao.saveOrder(orderState)
                    engine.newOrder(orderState)
                }
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
        val orderStates = orderDao.getOrders(customerDao.getCustomer(customerId))
        return createOrderStatesResponse(orderStates = orderStates.toTypedArray(), HttpStatus.OK)
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(OrdersApi::class.java)
    }
}